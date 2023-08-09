package com.hyouteki.projects.memey.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.adapters.GifAdapter
import com.hyouteki.projects.memey.classes.ApiCallSingleton
import com.hyouteki.projects.memey.classes.MemeyFragment
import com.hyouteki.projects.memey.comms.AdapterComms
import com.hyouteki.projects.memey.comms.FragMainComms
import com.hyouteki.projects.memey.dialogs.FullGifBottomSheet
import com.hyouteki.projects.memey.models.GifMvvm

class GifFragment : MemeyFragment("Gifs", MemeyFragment.NO_ACTION), AdapterComms {
    private lateinit var thisAdapter: GifAdapter
    private lateinit var comm: FragMainComms

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gif, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.rv_fg_recycler)
        thisAdapter = GifAdapter(this)
        comm = activity as FragMainComms
        loadGIF()
        recycler.adapter = thisAdapter
        return view
    }

    private fun loadGIF() {
        comm.showProgressBar()
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
        comm.dismissProgressBar()
    }

    override fun showFullGif(gif: GifMvvm) {
        val dialog = FullGifBottomSheet()
        val bundle = Bundle()
        bundle.putString("title", gif.title)
        bundle.putString("gifUrl", gif.gifUrl)
        dialog.arguments = bundle
        dialog.show(childFragmentManager, "customDialog")
    }

}