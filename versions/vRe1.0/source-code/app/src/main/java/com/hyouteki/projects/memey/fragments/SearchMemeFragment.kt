package com.hyouteki.projects.memey.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
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

class SearchMemeFragment : Fragment(), Communicator {
    private lateinit var searchView: SearchView
    private lateinit var progress: ProgressBar
    private lateinit var noImage: ImageView
    private lateinit var noText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MemeAdapter
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var comm: Frag2ActCommunicator

    private val sharedPref =
        this.activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    private val nsfwFilter = sharedPref?.getBoolean("nsfwFilter", false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_meme, container, false)
        searchView = view.findViewById(R.id.sv_fsm_search)
        progress = view.findViewById(R.id.pb_fsm_progress)
        noImage = view.findViewById(R.id.iv_fsm_no_image)
        noText = view.findViewById(R.id.tv_fsm_no_text)
        recyclerView = view.findViewById(R.id.rv_fsm_meme_recycler)
        myAdapter = MemeAdapter(this)
        comm = activity as Frag2ActCommunicator

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    loadMeme(it)
                    recyclerView.adapter = myAdapter
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                newText?.let { loadMeme(it) }
                return false
            }
        })
        return view
    }

    private fun loadMeme(text: String) {
        progress.visibility = View.VISIBLE
        noImage.visibility = View.INVISIBLE
        noText.visibility = View.INVISIBLE
        val url = "https://meme-api.com/gimme/${text.replace("\\s".toRegex(), "")}/50"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                progress.visibility = View.INVISIBLE
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
                    memeArray.add(randomMeme)
                }
                myAdapter.updateData(memeArray)
            },
            {
                myAdapter.updateData(ArrayList<RandomMeme>())
                progress.visibility = View.INVISIBLE
                noImage.visibility = View.VISIBLE
                noText.text = "Either r/${text.replace("\\s".toRegex(), "")} does not exist or does not contain memes"
                noText.visibility = View.VISIBLE
            })
        ApiCallSingleton.getInstance(requireContext())
            .addToRequestQueue(jsonObjectRequest)
    }

    override fun shareButtonClickMethod(meme: RandomMeme) {
        val shareIntent = Intent()
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