package com.hyouteki.projects.memey.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.adapters.MemeAdapter
import com.hyouteki.projects.memey.classes.ApiCallSingleton
import com.hyouteki.projects.memey.comms.AdapterComms
import com.hyouteki.projects.memey.daos.MemeDao
import com.hyouteki.projects.memey.databinding.ActivitySearchMemeBinding
import com.hyouteki.projects.memey.dialogs.FullTitleBottomSheet
import com.hyouteki.projects.memey.interfaces.Saver
import com.hyouteki.projects.memey.interfaces.Tags
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.models.RandomMeme
import kotlinx.android.synthetic.main.activity_search_meme.*
import kotlinx.android.synthetic.main.meme_list_item.*

class SearchMemeActivity : AppCompatActivity(), AdapterComms {
    private lateinit var binding: ActivitySearchMemeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: MemeAdapter
    private val user = FirebaseAuth.getInstance().currentUser!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchMemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.secBackground)
        initialize()
        handleTouches()
    }

    private fun handleTouches() {
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    loadMeme(it)
                    binding.recycler.adapter = adapter
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                newText?.let { loadMeme(it) }
                return false
            }
        })
    }

    private fun initialize() {
        sharedPreferences = Saver.getPreferences(this)
        binding.progress.visibility = View.GONE
        adapter = MemeAdapter(this)
    }

    @SuppressLint("SetTextI18n")
    private fun loadMeme(text: String) {
        binding.progress.visibility = View.VISIBLE
        binding.recycler.visibility = View.INVISIBLE
        val url = "https://meme-api.com/gimme/${text.replace("\\s".toRegex(), "")}/50"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            val memeJSONArray = response.getJSONArray("memes")
            val memeArray = ArrayList<RandomMeme>()
            for (i in 0 until memeJSONArray.length()) {
                val memeJsonObject = memeJSONArray.getJSONObject(i)
                val title = memeJsonObject.getString("title")
                val photoUrl = memeJsonObject.getString("url")
                val postUrl = memeJsonObject.getString("postLink")
                val nsfw = memeJsonObject.getBoolean("nsfw")
                val upvoteCount = memeJsonObject.getInt("ups")
                val liked = false
                val randomMeme =
                    RandomMeme(photoUrl, postUrl, title, nsfw, upvoteCount.toString(), liked)
                when (sharedPreferences.getBoolean(Saver.NSFW_FILTER, false)) {
                    true -> {
                        if (!nsfw) {
                            memeArray.add(randomMeme)
                        }
                    }
                    false -> memeArray.add(randomMeme)
                }
            }
            adapter.updateData(memeArray)
            binding.progress.visibility = View.GONE
            binding.recycler.visibility = View.VISIBLE
        }, {
            adapter.updateData(ArrayList<RandomMeme>())
            binding.progress.visibility = View.GONE
            binding.recycler.visibility = View.VISIBLE
            with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                setTitle(
                    "Either r/${
                        text.replace(
                            "\\s".toRegex(), ""
                        )
                    } does not exist or does not contain memes."
                )
                setPositiveButton("OK") { _, _ ->

                }
                show()
            }
        })
        ApiCallSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun showFullMemeTitle(title: String) {
        val dialog = FullTitleBottomSheet()
        val bundle = Bundle()
        bundle.putString("title", title)
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "FullTitle#BottomSheet@Memey")
    }

    override fun onShareMemeClick(meme: RandomMeme) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        when (sharedPreferences.getString(Saver.WHAT_TO_SHARE, Tags.IMAGE_LINK)) {
            Tags.IMAGE_LINK -> shareIntent.putExtra(Intent.EXTRA_TEXT, meme.photoUrl)
            else -> shareIntent.putExtra(Intent.EXTRA_TEXT, meme.postUrl)
        }
        startActivity(Intent.createChooser(shareIntent, "Send this amazing meme using..."))
    }

    override fun onFavoriteMemeClick(meme: RandomMeme) {
        if (!meme.liked) {
            meme.liked = true
            val currentTime = System.currentTimeMillis()
            MemeDao().addMeme(
                Meme(
                    user.uid,
                    currentTime.toString(),
                    meme.photoUrl,
                    meme.postUrl,
                    meme.title,
                    meme.nsfw,
                    meme.upvoteCount
                )
            )
        }
    }

    override fun onRedirectMemeClick(meme: Meme) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(meme.postUrl)
        startActivity(openURL)
    }
}