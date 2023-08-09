package com.hyouteki.projects.memey.activities

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import de.hdodenhof.circleimageview.CircleImageView

class PreviewMemeActivity : AppCompatActivity() {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_meme)
        window.statusBarColor = getColor(R.color.colorBackground)

        val userName = findViewById<TextView>(R.id.tv_apm_user_name)
        val userImage = findViewById<CircleImageView>(R.id.civ_apm_user_image)
        val title = findViewById<TextView>(R.id.tv_apm_title_text)
        val memeImage = findViewById<ImageView>(R.id.iv_apm_meme_image)
        val shareButton = findViewById<ImageView>(R.id.btn_apm_share)
        val favoriteButton = findViewById<ImageView>(R.id.btn_apm_favorite)
        val closeButton = findViewById<Button>(R.id.btn_apm_close)

        userName.text = currentUser.displayName
        Glide.with(userImage.context).load(currentUser.photoUrl).into(userImage)
        title.text = intent.getStringExtra("title")
        memeImage.setImageURI(Uri.parse(intent.getStringExtra("imageUrl")))
        closeButton.setOnClickListener { finish() }
    }
}