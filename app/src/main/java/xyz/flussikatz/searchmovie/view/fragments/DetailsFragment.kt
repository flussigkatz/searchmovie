package xyz.flussikatz.searchmovie.view.fragments

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
import xyz.flussikatz.searchmovie.data.entity.Film
import xyz.flussikatz.searchmovie.R
import xyz.flussikatz.searchmovie.data.ApiConstants
import xyz.flussikatz.searchmovie.util.AnimationHelper
import xyz.flussikatz.searchmovie.databinding.FragmentDetailsBinding
import xyz.flussikatz.searchmovie.viewmodel.DetailsFragmentViewModel


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    private val viewModel: DetailsFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.rootFragmentDetails

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val film = arguments?.get(KEY_FILM) as Film
        binding.film = film

        AnimationHelper.revealAnimation(binding.rootFragmentDetails, requireActivity())

        Picasso.get()
            .load(ApiConstants.IMAGES_URL + ApiConstants.IMAGE_FORMAT_W500 + film.posterId)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.wait)
            .error(R.drawable.err)
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


        binding.detailsBottomToolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_page -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentDetails,
                        requireActivity(),
                        R.id.action_global_homeFragment
                    )
                    true
                }
                R.id.history -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentDetails,
                        requireActivity(),
                        R.id.action_global_historyFragment
                    )
                    true
                }
                R.id.marked -> {
                    AnimationHelper.coverAnimation(
                        binding.rootFragmentDetails,
                        requireActivity(),
                        R.id.action_global_markedFragment
                    )
                    true
                }
                else -> false
            }
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
            requestPermission()
            return
        }
        MainScope().launch {
            binding.detailsProgressBar.isVisible = true
            val job = scope.async {
                viewModel.loadFilmPoster(
                    ApiConstants.IMAGES_URL +
                            ApiConstants.IMAGE_FORMAT_ORIGINAL +
                            film.posterId
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
                snackbar.anchorView = binding.detailsBottomToolbar
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
            binding.detailsProgressBar.isVisible = false
        }
    }

    companion object {
        private const val KEY_FILM = "film"
    }
}