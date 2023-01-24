package com.hyouteki.projects.memey.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.classes.GifFireStoreAdapter
import com.hyouteki.projects.memey.daos.GifDao
import com.hyouteki.projects.memey.dialogs.FavFullGifDialog
import com.hyouteki.projects.memey.dialogs.FullGifDialog
import com.hyouteki.projects.memey.models.Gif

class FavGifFragment : Fragment() {
    private lateinit var gifRecycler: RecyclerView
    private lateinit var myAdapter: GifFireStoreAdapter

    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val collection = GifDao().collection
    private val query = collection.whereEqualTo("uid", currentUser.uid)
        .orderBy("gid", Query.Direction.DESCENDING)
    private val recyclerViewOptions = FirestoreRecyclerOptions
        .Builder<Gif>()
        .setQuery(query, Gif::class.java)
        .build()

    companion object {
        private const val TAG = "FavGifFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gif, container, false)
        gifRecycler = view.findViewById(R.id.rv_fg_recycler)
        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        myAdapter = GifFireStoreAdapter(this, recyclerViewOptions)
        gifRecycler.adapter = myAdapter
    }

    override fun onStart() {
        super.onStart()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }

    fun openGif(gif: Gif) {
        val dialog = FavFullGifDialog()
        val bundle = Bundle()
        bundle.putString("title", gif.title)
        bundle.putString("gifUrl", gif.gifUrl)
        bundle.putString("gid", gif.gid)
        dialog.arguments = bundle
        dialog.show(childFragmentManager, "customDialog")
    }
}