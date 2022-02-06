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
import kotlinx.coroutines.*
import xyz.flussigkatz.searchmovie.data.entity.Film
import xyz.flussigkatz.searchmovie.R
import xyz.flussigkatz.searchmovie.data.ApiConstantsApp.IMAGES_URL
import xyz.flussigkatz.searchmovie.data.ApiConstantsApp.IMAGE_FORMAT_ORIGINAL
import xyz.flussigkatz.searchmovie.data.ApiConstantsApp.IMAGE_FORMAT_W500
import xyz.flussigkatz.searchmovie.databinding.FragmentDetailsBinding
import xyz.flussigkatz.searchmovie.viewmodel.DetailsFragmentViewModel


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private val scope = CoroutineScope(Dispatchers.IO)
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


        initProgressBarState()

        val film = arguments?.get(DETAILS_FILM_KEY) as Film
        binding.film = film


        Picasso.get()
            .load(IMAGES_URL + IMAGE_FORMAT_W500 + film.posterId)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_default_picture)
            .error(R.drawable.ic_default_picture)
            .into(binding.detailsPoster)

        binding.detailsFabDownloadPoster.setOnClickListener {
            performAsyncLoadOfPoster(film)
        }

        binding.detailsFab.setOnClickListener {
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

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun saveToGallery(bitmap: Bitmap, film: Film) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, film.title.handleSingleQuote())
                put(MediaStore.Images.Media.DISPLAY_NAME,
                    film.title.handleSingleQuote())
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED,
                    System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN,
                    System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SearchMovie")
            }
            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            val outputStream = contentResolver.openOutputStream(uri!!)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream!!.close()
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

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    private fun performAsyncLoadOfPoster(film: Film) {
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
                viewModel.loadFilmPoster(
                    IMAGES_URL + IMAGE_FORMAT_ORIGINAL + film.posterId
                )
            }
            val bitmap = job.await()
            if (bitmap != null) {
                saveToGallery(bitmap, film)
                val snackbar = Snackbar.make(
                    binding.root,
                    R.string.downladed_to_galery,
                    Snackbar.LENGTH_LONG
                )
//                snackbar.anchorView = binding.rootFragmentDetails
                snackbar.setAction(R.string.open) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.type = "image/*"
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
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
        viewModel.progressBarState.subscribe {
            binding.detailsProgressBar.isVisible = it
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val DETAILS_FILM_KEY = "details_film"
    }
}