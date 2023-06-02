package com.example.storyapp.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityUploadStoryBinding
import com.example.storyapp.model.UploadStoryResponse
import com.example.storyapp.networking.ApiClient
import com.example.storyapp.utils.UserPreference
import com.example.storyapp.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class UploadStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadStoryBinding
    private var getFile: File? = null
    private lateinit var locationManager: LocationManager

    private var latitude = 0.0
    private var longitude = 0.0

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val isGallery = intent.getBooleanExtra("isGallery", false)
        val image = intent.getStringExtra("picture")

        if (isGallery) {
            Log.e("TAG", "onCreate: $image")
            getFile = File(image.toString())
           Toast.makeText(this, "isGallery: $image", Toast.LENGTH_SHORT).show()
            binding.previewImageView.setImageURI(image?.toUri())
        }

        binding.toggleLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
                    return@setOnCheckedChangeListener
                }

               locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f) { location ->
                    latitude = location.latitude
                    longitude = location.longitude
                }
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                }
                Toast.makeText(this, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()
            } else {
                latitude = 0.0
                longitude = 0.0
                Toast.makeText(this, "Location is off", Toast.LENGTH_SHORT).show()
            }
        }


        binding.buttonUploadImage.setOnClickListener {
            uploadStory()
        }

        binding.previewImageView.setOnClickListener {
            startCameraX()
        }


    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File
            myFile?.let { file ->
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }

        }
    }


    private fun uploadStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            binding.buttonUploadImage.isEnabled = false
            binding.buttonUploadImage.text = R.string.app_text_loading_indicator.toString()


            val description = binding.description.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val apiClient = ApiClient()
            val apiService = apiClient.createApiService()

            val userPreferences = UserPreference(this)
            val userData = userPreferences.getUser()

            val uploadImageRequest = apiService.uploadStory("Bearer ${userData.token}",imageMultipart, description, latitude, longitude)
            uploadImageRequest.enqueue(object : Callback<UploadStoryResponse> {
                override fun onResponse(
                    call: Call<UploadStoryResponse>,
                    response: Response<UploadStoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                            Toast.makeText(this@UploadStoryActivity, responseBody?.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@UploadStoryActivity, MainActivity::class.java))
                            finish()
                    } else {
                        Toast.makeText(this@UploadStoryActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                    binding.buttonUploadImage.isEnabled = true
                    binding.buttonUploadImage.text = getString(R.string.app_text_upload_story)
                }
                override fun onFailure(call: Call<UploadStoryResponse>, t: Throwable) {
                    binding.buttonUploadImage.isEnabled = true
                    binding.buttonUploadImage.text = getString(R.string.app_text_upload_story)
                    Toast.makeText(this@UploadStoryActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })

        } else {
            Toast.makeText(this@UploadStoryActivity, "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@UploadStoryActivity, MainActivity::class.java))
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

}