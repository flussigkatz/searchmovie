package xyz.flussigkatz.searchmovie.view.fragments

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import xyz.flussigkatz.core_api.entity.BrowsingFilm
import xyz.flussigkatz.searchmovie.App
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ConstantsApp.DETAILS_FILM_KEY
import xyz.flussigkatz.searchmovie.data.ConstantsApp.IMAGES_URL
import xyz.flussigkatz.searchmovie.data.model.FilmUiModel
import xyz.flussigkatz.searchmovie.databinding.FragmentDetailsBinding
import xyz.flussigkatz.searchmovie.viewmodel.DetailsFragmentViewModel
import javax.inject.Inject

class DetailsFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: DetailsFragmentViewModel by viewModels { viewModelFactory }
    private lateinit var binding: FragmentDetailsBinding
    private val favoriteMarkState = MutableLiveData<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.rootFragmentDetails

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.inject(this)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(DETAILS_FILM_KEY, FilmUiModel::class.java)
                ?.let { initFilm(it) }
        } else arguments?.getParcelable<FilmUiModel>(DETAILS_FILM_KEY)?.let { initFilm(it) }
        initNavigationIcon()
    }

    private fun initFilm(film: FilmUiModel) {
        binding.film = film
        initDownloadFilmPoster(film)
        lifecycleScope.launch {
            viewModel.insertBrowsingFilm(BrowsingFilm(film))
            film.favState = viewModel.getFilmMarkStatus(film.id)
        }
        initFab(film)
    }

    private fun initFab(film: FilmUiModel) {
        favoriteMarkState.postValue(film.favState)
        favoriteMarkState.observe(viewLifecycleOwner) { favoriteMarkState ->
            lifecycleScope.launch {
                film.favState = viewModel.changeFavoriteMark(film.id, favoriteMarkState).also {
                    if (it) binding.detailsFabFavorite.setImageResource(R.drawable.ic_favorite)
                    else binding.detailsFabFavorite.setImageResource(R.drawable.ic_unfavorite)
                }
            }
        }
        binding.detailsFabFavorite.setOnClickListener {
            favoriteMarkState.postValue(!film.favState)
        }
        binding.detailsFabDownloadPoster.setOnClickListener {
            performAsyncLoadOfPoster(film)
        }
        binding.detailsFabShare.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check this film: ${film.title} \n ${film.description}."
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share to"))
        }
    }

    private fun checkPermission() {
        if (checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), R.string.no_permission, Toast.LENGTH_SHORT).show()
            requestPermissions(requireActivity(), arrayOf(WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
        }
    }

    private fun saveToGallery(bitmap: Bitmap, film: FilmUiModel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, film.title.handleSingleQuote())
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    film.title.handleSingleQuote()
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.Images.Media.DATE_ADDED,
                    System.currentTimeMillis() / 1000
                )
                put(
                    MediaStore.Images.Media.DATE_TAKEN,
                    System.currentTimeMillis()
                )
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SearchMovie")
            }
            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                val outputStream = contentResolver.openOutputStream(it)
                bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
                outputStream?.close()
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                film.title.handleSingleQuote(),
                film.description.handleSingleQuote()
            )
        }
    }

    private fun String.handleSingleQuote() = this.replace("'", "")

    private fun performAsyncLoadOfPoster(film: FilmUiModel) {
        checkPermission()
        lifecycleScope.launch(Dispatchers.Main) {
            binding.detailsProgressBar.isVisible = true
            val job = lifecycleScope.async(Dispatchers.IO) {
                viewModel.loadFilmPoster(IMAGES_URL + IMAGE_FORMAT_ORIGINAL + film.posterId)
            }
            val bitmap = job.await()
            bitmap?.let {
                saveToGallery(it, film)
                val snackbar = Snackbar.make(
                    binding.root,
                    R.string.downloaded_to_gallery,
                    Snackbar.LENGTH_LONG
                )
                snackbar.setAction(R.string.open) {
                    startActivity(
                        Intent().apply {
                            action = Intent.ACTION_VIEW
                            type = "image/*"
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                }.show()
            } ?: viewModel.postMessage(R.string.error_upload_message)
            binding.detailsProgressBar.isVisible = false
        }
    }

    private fun initNavigationIcon() {
        binding.detailsToolbar.setNavigationIcon(R.drawable.arrow_with_background)
        binding.detailsToolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    private fun initDownloadFilmPoster(film: FilmUiModel) {
        Picasso.get()
            .load(IMAGES_URL + IMAGE_FORMAT_W500 + film.posterId)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_default_picture)
            .error(R.drawable.ic_default_picture)
            .into(binding.detailsPoster)
    }

    companion object {
        private const val REQUEST_CODE = 1
        private const val IMAGE_QUALITY = 100
        private const val IMAGE_FORMAT_ORIGINAL = "original"
        private const val IMAGE_FORMAT_W500 = "w500"
    }
}