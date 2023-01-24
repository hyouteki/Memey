package com.hyouteki.projects.memey.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.classes.ApiCallSingleton
import com.hyouteki.projects.memey.classes.GifAdapter
import com.hyouteki.projects.memey.classes.GifCommunicator
import com.hyouteki.projects.memey.dialogs.FullGifDialog
import com.hyouteki.projects.memey.models.GifMvvm

class SearchGifFragment : Fragment(), GifCommunicator {
    private lateinit var searchView: SearchView
    private lateinit var progress: ProgressBar
    private lateinit var noImage: ImageView
    private lateinit var noText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: GifAdapter

    private val sharedPref =
        this.activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    private val nsfwFilter = sharedPref?.getBoolean("nsfwFilter", false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_gif, container, false)
        searchView = view.findViewById(R.id.sv_fsg_search)
        progress = view.findViewById(R.id.pb_fsg_progress)
        noImage = view.findViewById(R.id.iv_fsg_no_image)
        noText = view.findViewById(R.id.tv_fsg_no_text)
        recyclerView = view.findViewById(R.id.rv_fsg_gif_recycler)
        myAdapter = GifAdapter(this)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    loadGif(it)
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

    private fun loadGif(text: String) {
        progress.visibility = View.VISIBLE
        noImage.visibility = View.INVISIBLE
        noText.visibility = View.INVISIBLE
        if (text == "") {
            Toast.makeText(requireContext(), "Empty search feed is found", Toast.LENGTH_SHORT)
                .show()
        } else {
            val url =
                "https://api.giphy.com/v1/gifs/search?api_key=zSK0S3lt8ZfSMn1hI36Amodb5FSO6afn&limit=100&rating=g&q=$text"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, {
                progress.visibility = View.INVISIBLE
                val gifJSONArray = it.getJSONArray("data")
                val gifArray = arrayListOf<GifMvvm>()
                for (i in 0 until gifJSONArray.length()) {
                    val gifJsonObject = gifJSONArray.getJSONObject(i)
                    val temp1 = gifJsonObject.getJSONObject("images")
                    val temp2 = temp1.getJSONObject("downsized")

                    if (gifJsonObject.has("title") && temp2.has("url")) {
                        val gif = GifMvvm(temp2.getString("url"), gifJsonObject.getString("title"))
                        gifArray.add(gif)
                    }
                }
                myAdapter.updateData(gifArray)
                if (myAdapter.itemCount == 0) {
                    noImage.visibility = View.VISIBLE
                    noText.visibility = View.VISIBLE
                } else {
                    noImage.visibility = View.INVISIBLE
                    noText.visibility = View.INVISIBLE
                }
            }, {})
            ApiCallSingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)

        }
    }

    override fun openGif(gif: GifMvvm) {
        val dialog = FullGifDialog()
        val bundle = Bundle()
        bundle.putString("title", gif.title)
        bundle.putString("gifUrl", gif.gifUrl)
        dialog.arguments = bundle
        dialog.show(childFragmentManager, "customDialog")
    }

}