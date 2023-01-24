package com.hyouteki.projects.memey.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.classes.ApiCallSingleton
import com.hyouteki.projects.memey.classes.Communicator
import com.hyouteki.projects.memey.classes.Frag2ActCommunicator
import com.hyouteki.projects.memey.classes.MemeAdapter
import com.hyouteki.projects.memey.daos.MemeDao
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.models.RandomMeme

class MemeFragment : Fragment(), Communicator {
    private var nsfwFilter: Boolean = false
    private var memeCount: Int = 10
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MemeAdapter
    private lateinit var comm: Frag2ActCommunicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)

        val sharedPref = requireActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE)
        nsfwFilter = sharedPref.getBoolean("nsfwFilter", false)
        memeCount = sharedPref.getInt("memeCount", 10)

        comm = activity as Frag2ActCommunicator
        recyclerView = view.findViewById(R.id.rv_frv_recycler)
        myAdapter = MemeAdapter(this)
        loadMeme()
        recyclerView.adapter = myAdapter
        return view
    }

    private fun loadMeme() {
        val url = "https://meme-api.com/gimme/$memeCount"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
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
            },
            {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show()
            })
        ApiCallSingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
    }

    override fun shareButtonClickMethod(meme: RandomMeme) {
        comm.shareMeme(meme)
    }

    override fun favoriteButtonClickMethod(meme: RandomMeme) {
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

    override fun redirectButtonClickMethod(meme: RandomMeme) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(meme.postUrl)
        startActivity(openURL)
    }
}