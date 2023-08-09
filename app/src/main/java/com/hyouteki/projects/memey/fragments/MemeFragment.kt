package com.hyouteki.projects.memey.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.adapters.MemeAdapter
import com.hyouteki.projects.memey.classes.ApiCallSingleton
import com.hyouteki.projects.memey.classes.MemeyFragment
import com.hyouteki.projects.memey.comms.AdapterComms
import com.hyouteki.projects.memey.comms.FragMainComms
import com.hyouteki.projects.memey.daos.MemeDao
import com.hyouteki.projects.memey.dialogs.FullTitleBottomSheet
import com.hyouteki.projects.memey.interfaces.Saver
import com.hyouteki.projects.memey.interfaces.Tags
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.models.RandomMeme

class MemeFragment : MemeyFragment("Memes", MemeyFragment.REFRESH_ACTION), AdapterComms {
    private var nsfwFilter: Boolean = false
    private var memeCount: Int = 10
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MemeAdapter
    private lateinit var main: FragMainComms
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)

        sharedPreferences = Saver.getPreferences(requireContext())
        main = activity as FragMainComms
        recyclerView = view.findViewById(R.id.rv_frv_recycler)
        myAdapter = MemeAdapter(this)
        loadMeme()
        recyclerView.adapter = myAdapter
        return view
    }

    private fun loadMeme() {
        nsfwFilter = sharedPreferences.getBoolean(Saver.NSFW_FILTER, false)
        memeCount = sharedPreferences.getInt(Saver.MEME_COUNT, 10)
        val url = "https://meme-api.com/gimme/$memeCount"
        main.showProgressBar()
        recyclerView.visibility = View.INVISIBLE
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
                if (nsfwFilter) {
                    if (!nsfw) {
                        memeArray.add(randomMeme)
                    }
                } else {
                    memeArray.add(randomMeme)
                }
            }
            myAdapter.updateData(memeArray)
        }, {
            Toast.makeText(requireContext(), "Internet connection is unstable", Toast.LENGTH_LONG).show()
        })
        ApiCallSingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
        main.dismissProgressBar()
        recyclerView.visibility = View.VISIBLE
    }

    override fun showFullMemeTitle(title: String) {
        val dialog = FullTitleBottomSheet()
        val bundle = Bundle()
        bundle.putString("title", title)
        dialog.arguments = bundle
        dialog.show(childFragmentManager, "FullTitle#BottomSheet@Memey")
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
                    currentUser?.uid!!,
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