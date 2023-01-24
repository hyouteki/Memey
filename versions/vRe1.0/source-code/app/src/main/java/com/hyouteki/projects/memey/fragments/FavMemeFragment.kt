package com.hyouteki.projects.memey.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.classes.Communicator
import com.hyouteki.projects.memey.classes.Frag2ActCommunicator
import com.hyouteki.projects.memey.classes.MemeFireStoreAdapter
import com.hyouteki.projects.memey.daos.MemeDao
import com.hyouteki.projects.memey.models.Meme

class FavMemeFragment : Fragment() {
    private lateinit var memeRecycler: RecyclerView
    private lateinit var myAdapter: MemeFireStoreAdapter
    private lateinit var comm: Frag2ActCommunicator

    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val collection = MemeDao().collection
    private val query = collection.whereEqualTo("uid", currentUser.uid)
        .orderBy("mid", Query.Direction.DESCENDING)
    private val recyclerViewOptions = FirestoreRecyclerOptions
        .Builder<Meme>()
        .setQuery(query, Meme::class.java)
        .build()

    companion object {
        private const val TAG = "FavMemeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        memeRecycler = view.findViewById(R.id.rv_frv_recycler)
        comm = activity as Frag2ActCommunicator
        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        myAdapter = MemeFireStoreAdapter(this, recyclerViewOptions)
        memeRecycler.adapter = myAdapter
    }

    override fun onStart() {
        super.onStart()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }

    fun favoriteButtonClickMethod(meme: Meme) {
        collection.document(meme.mid)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                comm.refreshFavMemeFragment()
            }
            .addOnFailureListener {
                Log.d(TAG, "DocumentSnapshot successfully failed!")
            }
    }

    fun shareButtonClickMethod(meme: Meme) {
        comm.shareMeme(meme)
    }

    fun redirectButtonClickMethod(meme: Meme) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(meme.postUrl)
        startActivity(openURL)
    }

}