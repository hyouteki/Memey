package com.hyouteki.projects.memey.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.ktx.Firebase
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.daos.GifDao
import com.hyouteki.projects.memey.daos.MemeDao
import com.hyouteki.projects.memey.databinding.ActivityMoreBinding
import com.hyouteki.projects.memey.interfaces.Logger
import kotlinx.android.synthetic.main.activity_more.view.*


class MoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMoreBinding
    private val user = FirebaseAuth.getInstance().currentUser!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.secBackground)

        initialize()
        handleTouches()
    }

    private fun handleTouches() {
        binding.back.setOnClickListener { finish() }
        binding.meme.setOnClickListener {
            startActivity(Intent(this, FavoriteMemeActivity::class.java))
        }
        binding.add.setOnClickListener {
            startActivity(Intent(this, AddMemeActivity::class.java))
        }
        binding.gifCard.setOnClickListener {
            startActivity(Intent(this, FavoriteGifActivity::class.java))
        }
        binding.settingsCard.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.contactSupportCard.setOnClickListener {
            startActivity(Intent(this, ContactSupportActivity::class.java))
        }
        binding.aboutUsCard.setOnClickListener {
            startActivity(Intent(this, AboutUsActivity::class.java))
        }
        binding.myMemeCard.setOnClickListener {
            startActivity(Intent(this, MyMemesActivity::class.java))
        }
        binding.inviteCard.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey download this cool app called MEMEY. From this link https://github.com/Hyouteki/Memey/raw/main/Memey.apk"
            )
            startActivity(Intent.createChooser(shareIntent, "Invite a Hommie using..."))
        }
        binding.helpCard.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Hyouteki/Memey/")
                )
            )
        }
        binding.signOutCard.setOnClickListener {
            with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                setTitle("Confirm sign-out")
                setMessage("Are you sure you want to sign-out?")
                setPositiveButton("Yes") { _, _ ->
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                        .build()
                    val gsc = GoogleSignIn.getClient(this@MoreActivity, gso)
                    val auth = Firebase.auth
                    FirebaseAuth.getInstance().signOut()
                    auth.signOut()
                    gsc.signOut()
                    Log.d("onReceive", "Logout in progress")
                    val broadcastIntent = Intent()
                    broadcastIntent.action = "com.package.ACTION_LOGOUT"
                    sendBroadcast(broadcastIntent)
                    finish()
                }
                setNegativeButton("No") { _, _ ->

                }
                show()
            }
        }
    }

    private fun initialize() {
        binding.userName.text = user.displayName
        binding.userEmail.text = user.email
        Glide.with(binding.userImage.context).load(user.photoUrl).into(binding.userImage)
        MemeDao().collection.whereEqualTo("uid", user.uid).count().get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Count fetched successfully
                    val count = task.result.count
                    binding.memeCount.text = count.toString()
                } else {
                    Logger.debugger("Count favorite memes failed")
                }
            }
        GifDao().collection.whereEqualTo("uid", user.uid).count().get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Count fetched successfully
                    val count = task.result.count
                    binding.gifCount.text = count.toString()
                } else {
                    Logger.debugger("Count favorite gifs failed")
                }
            }
    }
}