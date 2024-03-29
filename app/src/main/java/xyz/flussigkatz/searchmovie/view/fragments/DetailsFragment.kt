package xyz.flussigkatz.searchmovie.view.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.*
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.AbstractFilmEntity
import xyz.flussigkatz.core_api.entity.BrowsingFilm
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ConstantsApp.IMAGES_URL
import xyz.flussigkatz.searchmovie.data.ConstantsApp.IMAGE_FORMAT_ORIGINAL
import xyz.flussigkatz.searchmovie.data.ConstantsApp.IMAGE_FORMAT_W500
import xyz.flussigkatz.searchmovie.databinding.FragmentDetailsBinding
import xyz.flussigkatz.searchmovie.util.AutoDisposable
import xyz.flussigkatz.searchmovie.util.addTo
import xyz.flussigkatz.searchmovie.viewmodel.DetailsFragmentViewModel


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    private val favoriteMarkState: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private val autoDisposable = AutoDisposable()
    private val viewModel: DetailsFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.rootFragmentDetails

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        autoDisposable.bindTo(lifecycle)
        initFilm(arguments?.get(DETAILS_FILM_KEY) as AbstractFilmEntity)
        initProgressBarState()
        initNavigationIcon()
    }

    private fun initFilm(film: AbstractFilmEntity) {
        binding.film = film
        initDownloadFilmPoster(film)
        viewModel.getFilmMarkStatusFromApi(film.id)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = { Timber.d(it) },
                onComplete = {},
                onNext = {
                    film.fav_state = it.itemPresent
                    initFab(film)
                    viewModel.putBrowsingFilmToDB(
                        BrowsingFilm(
                            id = film.id,
                            title = film.title,
                            posterId = film.posterId,
                            description = film.description,
                            rating = film.rating,
                            fav_state = film.fav_state
                        )
                    )
                }
            ).addTo(autoDisposable)
    }

    private fun initFab(film: AbstractFilmEntity) {
        favoriteMarkState.onNext(film.fav_state)
        favoriteMarkState.subscribeBy(
            onError = { Timber.d(it) },
            onNext = {
                film.fav_state = it
                if (it) binding.detailsFabFavorite.setImageResource(R.drawable.ic_favorite)
                else binding.detailsFabFavorite.setImageResource(R.drawable.ic_unfavorite)
            }
        ).addTo(autoDisposable)
        binding.detailsFabFavorite.setOnClickListener {
            binding.film?.let {
                it.fav_state = !film.fav_state
                favoriteMarkState.onNext(it.fav_state)
                if (film.fav_state) viewModel.addFavoriteFilmToList(it.id)
                else viewModel.removeFavoriteFilmFromList(it.id)
            }
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

    private fun checkPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun saveToGallery(bitmap: Bitmap, film: AbstractFilmEntity) {
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
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

    private fun performAsyncLoadOfPoster(film: AbstractFilmEntity) {
        if (!checkPermission()) {
            Toast.makeText(
                requireContext(), R.string.permission_storage, Toast.LENGTH_SHORT
            ).show()
            requestPermission()
            return
        }
        MainScope().launch {
            viewModel.progressBarState.onNext(true)
            val job = scope.async {
                viewModel.loadFilmPoster(IMAGES_URL + IMAGE_FORMAT_ORIGINAL + film.posterId)
            }
            val bitmap = job.await()
            if (bitmap != null) {
                saveToGallery(bitmap, film)
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
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.error_upload_message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            viewModel.progressBarState.onNext(false)
        }
    }

    private fun initProgressBarState() {
        viewModel.progressBarState.subscribeBy(
            onError = { Timber.d(it) },
            onNext = { binding.detailsProgressBar.isVisible = it }
        ).addTo(autoDisposable)
    }

    private fun initNavigationIcon() {
        binding.detailsToolbar.setNavigationIcon(R.drawable.arrow_with_background)
        binding.detailsToolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    private fun initDownloadFilmPoster(film: AbstractFilmEntity) {
        Picasso.get()
            .load(IMAGES_URL + IMAGE_FORMAT_W500 + film.posterId)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_default_picture)
            .error(R.drawable.ic_default_picture)
            .into(binding.detailsPoster)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val DETAILS_FILM_KEY = "details_film"
    }
}