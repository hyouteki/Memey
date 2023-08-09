package com.hyouteki.projects.memey.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.background)
        handleTouches()
    }

    private fun handleTouches() {
        binding.github.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://github.com/Hyouteki")
            startActivity(openURL)
        }
        binding.linkedIn.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://www.linkedin.com/in/lakshay-chauhan-319200239/")
            startActivity(openURL)
        }
        binding.instagram.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://www.instagram.com/mainlakshayhoon/")
            startActivity(openURL)
        }
        binding.memeApi.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://github.com/D3vd/Meme_Api")
            startActivity(openURL)
        }
        binding.giphyCard.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://developers.giphy.com/")
            startActivity(openURL)
        }
    }
}