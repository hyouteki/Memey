package com.hyouteki.projects.memey.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.classes.Frag2ActCommunicator
import com.hyouteki.projects.memey.dialogs.SignOutDialog
import com.hyouteki.projects.memey.fragments.*
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.models.RandomMeme
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity(), Frag2ActCommunicator {
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var actionButton: ImageView
    private lateinit var titleText: TextView
    private lateinit var sharedPref: SharedPreferences
    private var actionId: Int = 0


    companion object {
        private const val SEARCH_MEME_ACTION = 2
        private const val SEARCH_GIF_ACTION = 3
        private const val REFRESH_RANDOM_MEME_ACTION = 4
        private const val REFRESH_SEARCH_MEME_ACTION = 5
        private const val REFRESH_GIF_ACTION = 6
        private const val NO_ACTION = 7
        private const val BOTH_ACTION = 8
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.dl_am_drawer)
        val menuButton = findViewById<ImageView>(R.id.iv_am_menu)
        titleText = findViewById(R.id.tv_am_title)
        val navigationView = findViewById<NavigationView>(R.id.nv_am_navigationView)
        val headerView: View = navigationView.getHeaderView(0)
        val userImage = headerView.findViewById<CircleImageView>(R.id.civ_amnh_user_image)
        val userName = headerView.findViewById<TextView>(R.id.tv_amnh_user_name)
        val userEmail = headerView.findViewById<TextView>(R.id.tv_amnh_user_email)
        actionButton = findViewById(R.id.iv_am_action)

        sharedPref = this.getSharedPreferences("preferences", Context.MODE_PRIVATE)

        val currentTab = sharedPref.getString("currentTab", "Meme")
        if (currentTab == "Meme") {
            actionId = REFRESH_RANDOM_MEME_ACTION
            titleText.text = "Random memes"
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl_am_frame, MemeFragment())
                commit()
            }
        } else if (currentTab == "Gif") {
            actionId = REFRESH_GIF_ACTION
            titleText.text = "Trending gifs"
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl_am_frame, GifFragment())
                commit()
            }
        }

        changeActionImage()
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        actionButton.setOnClickListener {
            if (actionId == REFRESH_RANDOM_MEME_ACTION) {
                actionId = REFRESH_RANDOM_MEME_ACTION
                titleText.text = "Random memes"
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_am_frame, MemeFragment())
                    commit()
                }
            } else if (actionId == REFRESH_SEARCH_MEME_ACTION) {
                actionId = REFRESH_RANDOM_MEME_ACTION
                titleText.text = "Search memes"
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_am_frame, SearchMemeFragment())
                    commit()
                }
            }  else if (actionId == REFRESH_GIF_ACTION) {
                titleText.text = "Search gifs"
                actionId = REFRESH_GIF_ACTION
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_am_frame, SearchGifFragment())
                    commit()
                }
            }
        }

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_am_meme -> {
                    titleText.text = "Random memes"
                    actionId = REFRESH_RANDOM_MEME_ACTION
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_am_frame, MemeFragment())
                        commit()
                    }
                }
                R.id.menu_am_gif -> {
                    titleText.text = "Trending gifs"
                    actionId = NO_ACTION
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_am_frame, GifFragment())
                        commit()
                    }
                }
                R.id.menu_am_fav_meme -> {
                    titleText.text = "Favorite memes"
                    actionId = NO_ACTION
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_am_frame, FavMemeFragment())
                        commit()
                    }
                }
                R.id.menu_am_search_meme -> {
                    titleText.text = "Search memes"
                    actionId = REFRESH_SEARCH_MEME_ACTION
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_am_frame, SearchMemeFragment())
                        commit()
                    }
                }
                R.id.menu_am_fav_gif -> {
                    titleText.text = "Favorite gifs"
                    actionId = NO_ACTION
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_am_frame, FavGifFragment())
                        commit()
                    }
                }
                R.id.menu_am_search_gif -> {
                    titleText.text = "Search gifs"
                    actionId = REFRESH_GIF_ACTION
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_am_frame, SearchGifFragment())
                        commit()
                    }
                }
                R.id.menu_am_contact_support -> {
                    titleText.text = "Contact support"
                    actionId = NO_ACTION
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_am_frame, ContactSupportFragment())
                        commit()
                    }
                }
                R.id.menu_am_about_us -> {
                    titleText.text = "About us"
                    actionId = NO_ACTION
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_am_frame, AboutUsFragment())
                        commit()
                    }
                }
                R.id.menu_am_invite -> {
                    actionId = NO_ACTION
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hey download this cool app called memey. From this link https://github.com/Hyouteki/Memey/raw/main/Memey.apk"
                    )
                    startActivity(Intent.createChooser(shareIntent, "Invite a friend using..."))
                }
                R.id.menu_am_settings -> {
                    titleText.text = "Settings"
                    actionId = NO_ACTION
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_am_frame, SettingsFragment())
                        commit()
                    }
                }
                R.id.menu_am_sign_out -> {
                    actionId = NO_ACTION
                    SignOutDialog().show(supportFragmentManager, "customDialog")
                }
            }
            drawerLayout.closeDrawer(Gravity.START)
            changeActionImage()
            true
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("com.package.ACTION_LOGOUT")
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("onReceive", "Logout in progress")
                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                finish()
            }
        }, intentFilter)

        Glide.with(userImage.context)
            .load(user?.photoUrl)
            .circleCrop()
            .dontAnimate()
            .placeholder(R.drawable.ic_person)
            .into(userImage)

        userName.text = user?.displayName
        userEmail.text = user?.email
    }

    private fun changeActionImage() {
        when (actionId) {
            REFRESH_RANDOM_MEME_ACTION, REFRESH_SEARCH_MEME_ACTION, REFRESH_GIF_ACTION -> {
                actionButton.visibility = View.VISIBLE
                actionButton.setImageResource(R.drawable.ic_refresh)
            }
            SEARCH_GIF_ACTION -> {
                actionButton.visibility = View.INVISIBLE
                actionButton.setImageResource(R.drawable.ic_search)
            }
            NO_ACTION -> {
                actionButton.visibility = View.INVISIBLE
            }
        }
    }

    override fun refreshFavMemeFragment() {
        titleText.text = "Favorite memes"
        actionId = SEARCH_MEME_ACTION
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_am_frame, FavMemeFragment())
            commit()
        }
    }

    override fun refreshFavGifFragment() {
        titleText.text = "Favorite gifs"
        actionId = SEARCH_GIF_ACTION
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_am_frame, FavGifFragment())
            commit()
        }
    }

    override fun shareMeme(meme: Meme) {
        val get = sharedPref.getString("shareLink", "Image link")
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        if (get == "Image link") {
            shareIntent.putExtra(Intent.EXTRA_TEXT, meme.photoUrl)
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, meme.postUrl)
        }
        startActivity(Intent.createChooser(shareIntent, "Send this amazing meme using..."))
    }

    override fun shareMeme(meme: RandomMeme) {
        val get = sharedPref.getString("shareLink", "Image link")
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        if (get == "Image link") {
            shareIntent.putExtra(Intent.EXTRA_TEXT, meme.photoUrl)
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, meme.postUrl)
        }
        startActivity(Intent.createChooser(shareIntent, "Send this amazing meme using..."))
    }
}