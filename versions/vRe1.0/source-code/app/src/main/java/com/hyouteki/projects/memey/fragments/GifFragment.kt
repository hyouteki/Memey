package com.hyouteki.projects.memey.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.classes.ApiCallSingleton
import com.hyouteki.projects.memey.classes.GifAdapter
import com.hyouteki.projects.memey.classes.GifCommunicator
import com.hyouteki.projects.memey.dialogs.ConfirmDialog
import com.hyouteki.projects.memey.dialogs.FullGifDialog
import com.hyouteki.projects.memey.models.GifMvvm

class GifFragment : Fragment(), GifCommunicator {
    private lateinit var thisAdapter: GifAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gif, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.rv_fg_recycler)
        thisAdapter = GifAdapter(this)
        loadGIF()
        recycler.adapter = thisAdapter
        return view
    }

    private fun loadGIF() {
        val url =
            "https://api.giphy.com/v1/gifs/trending?api_key=zSK0S3lt8ZfSMn1hI36Amodb5FSO6afn&limit=100&rating=g"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, {
            val gifJSONArray = it.getJSONArray("data")
            val gifArray = arrayListOf<GifMvvm>()
            for (i in 0 until gifJSONArray.length()) {
                val gifJsonObject = gifJSONArray.getJSONObject(i)
                val temp1 = gifJsonObject.getJSONObject("images")
                val temp2 = temp1.getJSONObject("downsized")
                if (temp2.has("url")) {
                    val gif = GifMvvm(temp2.getString("url"), gifJsonObject.getString("title"))
                    gifArray.add(gif)
                }
            }
            thisAdapter.updateData(gifArray)
        }, {
        })
        ApiCallSingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
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