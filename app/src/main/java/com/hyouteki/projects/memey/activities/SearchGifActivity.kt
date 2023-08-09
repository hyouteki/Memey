package com.hyouteki.projects.memey.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.adapters.GifAdapter
import com.hyouteki.projects.memey.classes.ApiCallSingleton
import com.hyouteki.projects.memey.comms.AdapterComms
import com.hyouteki.projects.memey.comms.BottomSheetComms
import com.hyouteki.projects.memey.databinding.ActivitySearchGifBinding
import com.hyouteki.projects.memey.dialogs.FullGifBottomSheet
import com.hyouteki.projects.memey.models.GifMvvm

class SearchGifActivity : AppCompatActivity(), AdapterComms, BottomSheetComms {
    private lateinit var binding: ActivitySearchGifBinding
    private lateinit var adapter: GifAdapter
    private var currentGif: GifMvvm? = null
    private var favorite: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchGifBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.secBackground)
        initialize()
        handleTouches()
    }

    private fun handleTouches() {
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    loadGif(it)
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
        binding.progress.visibility = View.GONE
        adapter = GifAdapter(this)
    }


    private fun loadGif(text: String) {
        binding.progress.visibility = View.VISIBLE
        binding.recycler.visibility = View.INVISIBLE
        if (text == "") {
            Toast.makeText(this, "Empty search feed is found", Toast.LENGTH_SHORT).show()
        } else {
            val url =
                "https://api.giphy.com/v1/gifs/search?api_key=zSK0S3lt8ZfSMn1hI36Amodb5FSO6afn&limit=100&rating=g&q=$text"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, {
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
                adapter.updateData(gifArray)
                if (adapter.itemCount == 0) {
                    with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                        setTitle("No gifs found; Sorry!")
                        setPositiveButton("OK") { _, _ ->

                        }
                        show()
                    }
                }
                binding.progress.visibility = View.GONE
                binding.recycler.visibility = View.VISIBLE
            }, {})
            ApiCallSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
        }
    }

    override fun showFullGif(gif: GifMvvm) {
        val dialog = FullGifBottomSheet()
        val bundle = Bundle()
        bundle.putString("title", gif.title)
        bundle.putString("gifUrl", gif.gifUrl)
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "customDialog")
        currentGif = gif
    }

    override fun onFavoriteGifClick() {}

}