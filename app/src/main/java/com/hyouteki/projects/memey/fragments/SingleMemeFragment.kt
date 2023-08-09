package com.hyouteki.projects.memey.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.classes.ApiCallSingleton
import com.hyouteki.projects.memey.classes.MemeyFragment
import com.hyouteki.projects.memey.comms.FragMainComms
import com.hyouteki.projects.memey.daos.MemeDao
import com.hyouteki.projects.memey.databinding.FragmentSingleMemeBinding
import com.hyouteki.projects.memey.interfaces.Helper
import com.hyouteki.projects.memey.interfaces.Saver
import com.hyouteki.projects.memey.interfaces.Tags
import com.hyouteki.projects.memey.models.Meme
import kotlin.math.abs


class SingleMemeFragment : MemeyFragment("Memes", MemeyFragment.REFRESH_ACTION) {
    private lateinit var main: FragMainComms
    private val user = FirebaseAuth.getInstance().currentUser!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentSingleMemeBinding
    private var memeTitle: String = ""
    private var photoUrl: String = ""
    private var postUrl: String = ""
    private var nsfw: Boolean = false
    private var upvoteCount = 0
    private var liked: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSingleMemeBinding.inflate(inflater)

        initialize()
        loadMeme()
        handleTouches()
        gestureControl()

        return binding.root
    }

    private fun handleTouches() {
        binding.next.setOnClickListener { loadMeme() }
        binding.favorite.setOnClickListener { favorite() }
        binding.share.setOnClickListener { share() }
        binding.redirect.setOnClickListener { redirect() }
        binding.upvote.setOnClickListener {
            Helper.makeToast(requireContext(), "upvote")
        }
    }

    private fun favorite() {
        binding.favoriteIcon.setImageResource(R.drawable.ic_favorite_filled)
        if (!liked) {
            liked = true
            val currentTime = System.currentTimeMillis()
            MemeDao().addMeme(
                Meme(
                    user.uid,
                    currentTime.toString(),
                    photoUrl,
                    postUrl,
                    memeTitle,
                    nsfw,
                    upvoteCount.toString()
                )
            )
        }
    }

    private fun share() {
        val get = sharedPreferences.getString("shareLink", "Image link")
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        if (get == "Image link") {
            shareIntent.putExtra(Intent.EXTRA_TEXT, photoUrl)
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, postUrl)
        }
        startActivity(Intent.createChooser(shareIntent, "Send this amazing meme using..."))
    }

    private fun redirect() {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(postUrl)
        startActivity(openURL)
    }

    private fun initialize() {
        sharedPreferences = Saver.getPreferences(requireContext())
        main = activity as FragMainComms
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun gestureControl() {
        val gesture = GestureDetector(
            activity,
            object : SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent) = true
                override fun onFling(
                    e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    val swipeMinDistance = 60
                    val swipeMaxOffDistance = 150
                    val swipeThresholdVelocity = 100
                    try {
                        if (abs(e1.y - e2.y) > swipeMaxOffDistance) return false
                        if (e1.x - e2.x > swipeMinDistance
                            && abs(velocityX) > swipeThresholdVelocity
                        ) {
                            swipeRightGesture()
                        } else if (e2.x - e1.x > swipeMinDistance
                            && abs(velocityX) > swipeThresholdVelocity
                        ) {
                            swipeLeftGesture()
                        }
                    } catch (_: Exception) {
                    }
                    return super.onFling(e1, e2, velocityX, velocityY)
                }
            })
        binding.root.setOnTouchListener(OnTouchListener { _, event -> gesture.onTouchEvent(event) })
    }


    private fun loadMeme() {
        val nsfwFilter = sharedPreferences.getBoolean(Saver.NSFW_FILTER, false)
        val url = "https://meme-api.com/gimme/1"
        main.showProgressBar()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            val memeJSONArray = response.getJSONArray("memes")
            val memeJsonObject = memeJSONArray.getJSONObject(0)
            memeTitle = memeJsonObject.getString("title")
            photoUrl = memeJsonObject.getString("url")
            postUrl = memeJsonObject.getString("postLink")
            nsfw = memeJsonObject.getBoolean("nsfw")
            upvoteCount = memeJsonObject.getInt("ups")
            liked = false
            if (nsfwFilter) {
                if (!nsfw) {
                    initializeMeme()
                }
            } else {
                initializeMeme()
            }
        }, {
            Toast.makeText(requireContext(), "Internet connection is unstable", Toast.LENGTH_LONG)
                .show()
        })
        ApiCallSingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
        main.dismissProgressBar()
    }

    private fun initializeMeme() {
        binding.title.text = memeTitle
        binding.favoriteIcon.setImageResource(R.drawable.ic_favorite)
        Glide.with(binding.memeImage.context)
            .load(photoUrl)
            .into(binding.memeImage)
        binding.upvoteCount.text = upvoteCount.toString()
    }

    private fun swipeLeftGesture() {
        when (sharedPreferences.getString(Saver.SWIPE_LEFT_GESTURE, Tags.FAVORITE_MEME)) {
            Tags.FAVORITE_MEME -> favorite()
            Tags.SHARE_MEME -> share()
            else -> redirect()
        }
    }

    private fun swipeRightGesture() {
        when (sharedPreferences.getString(Saver.SWIPE_RIGHT_GESTURE, Tags.SHARE_MEME)) {
            Tags.FAVORITE_MEME -> favorite()
            Tags.SHARE_MEME -> share()
            else -> redirect()
        }

    }
}