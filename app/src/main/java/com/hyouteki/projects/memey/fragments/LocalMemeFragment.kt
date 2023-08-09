package com.hyouteki.projects.memey.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.adapters.LocalMemeFireStoreAdapter
import com.hyouteki.projects.memey.classes.MemeyFragment
import com.hyouteki.projects.memey.comms.FragMainComms
import com.hyouteki.projects.memey.interfaces.Communicator
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.viewmodels.MemeyViewModel

class LocalMemeFragment : MemeyFragment("MEMEY memes", MemeyFragment.ADD_ACTION) {
    private lateinit var comm: Communicator
    private lateinit var main: FragMainComms
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: LocalMemeFireStoreAdapter
    private val query = MemeyViewModel
        .localMemeCollection
        .orderBy("mid", Query.Direction.DESCENDING)
    private val recyclerViewOptions = FirestoreRecyclerOptions
        .Builder<Meme>()
        .setQuery(query, Meme::class.java)
        .build()
    private val sharedPref =
        this.activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    private val nsfwFilter = sharedPref?.getBoolean("nsfwFilter", false)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_local_meme, container, false)
        recyclerView = view.findViewById(R.id.rv_flm_recycler)
        comm = activity as Communicator
        main = activity as FragMainComms
        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        main.showProgressBar()
        myAdapter = LocalMemeFireStoreAdapter(comm, recyclerViewOptions, false)
        recyclerView.adapter = myAdapter
        main.dismissProgressBar()
    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        setupRecyclerView()
        myAdapter.stopListening()
    }
}