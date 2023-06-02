package com.example.storyapp.view

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.utils.UserPreference
import com.example.storyapp.view.adapter.ListStoryAdapter
import com.example.storyapp.view.adapter.LoadingStateAdapter
import com.example.storyapp.viewmodel.ListStoryViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

var INI_TOKEN = ""

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var token = ""

private val listStoryViewModel: ListStoryViewModel by viewModels {
   ViewModelFactory(this, token)
}


    companion object {
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
        super .onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val userPreference = UserPreference(this@MainActivity)
        val sharedPreferences = userPreference.getUser()

        if (sharedPreferences.token == "") {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            token = sharedPreferences.token.toString()
            INI_TOKEN = sharedPreferences.token.toString()
        }

        binding.buttonAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadStoryActivity::class.java)
            startActivity(intent)
        }

        binding.mapButton.setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.logoutButton.setOnClickListener {
            val logout = userPreference.clearUser()
            if (logout) {
                Toast.makeText(this@MainActivity, "Logout Success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.putExtra("isLogout", true)
                startActivity(intent)
                finish()
            }

        }
        binding.rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
        getData()

//        listStoryViewModel.stories.observe(this) {
//            val adapter = ListStoryAdapter()
//            adapter.setList(it.listStory as ArrayList<ListStoryItem>)
//            binding.rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
//            binding.rvStory.adapter = adapter
//        }
//
//        listStoryViewModel.getStories(sharedPreferences.token.toString())
//
//        listStoryViewModel.isLoading.observe(this) {
//            binding.isLoading.visibility = if (it) android.view.View.VISIBLE else android.view.View.GONE
//        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (sharedPreferences.token != "") {
                    finishAffinity()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }

    private fun getData() {
        val adapter = ListStoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        listStoryViewModel.story.observe(this@MainActivity) {
            adapter.submitData(lifecycle, it)
        }

    }
}









