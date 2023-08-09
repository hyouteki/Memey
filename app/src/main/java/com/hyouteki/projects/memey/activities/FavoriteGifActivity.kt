package com.hyouteki.projects.memey.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.adapters.GifFireStoreAdapter
import com.hyouteki.projects.memey.comms.AdapterComms
import com.hyouteki.projects.memey.comms.BottomSheetComms
import com.hyouteki.projects.memey.daos.GifDao
import com.hyouteki.projects.memey.databinding.ActivityFavoriteGifBinding
import com.hyouteki.projects.memey.dialogs.FullGifBottomSheet
import com.hyouteki.projects.memey.interfaces.Logger
import com.hyouteki.projects.memey.interfaces.Saver
import com.hyouteki.projects.memey.models.Gif

class FavoriteGifActivity : AppCompatActivity(), AdapterComms, BottomSheetComms {
    private lateinit var binding: ActivityFavoriteGifBinding
    private lateinit var adapter: GifFireStoreAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var currentGif: Gif? = null
    private val user = FirebaseAuth.getInstance().currentUser!!
    private val collection = GifDao().collection
    private val query = collection.whereEqualTo("uid", user.uid)
        .orderBy("gid", Query.Direction.DESCENDING)
    private val recyclerViewOptions = FirestoreRecyclerOptions
        .Builder<Gif>()
        .setQuery(query, Gif::class.java)
        .build()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteGifBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.secBackground)
        sharedPreferences = Saver.getPreferences(this)
        setupRecyclerView()
        handleTouches()
    }

    private fun setupRecyclerView() {
        binding.progress.visibility = View.VISIBLE
        binding.recycler.visibility = View.INVISIBLE
        adapter = GifFireStoreAdapter(this, recyclerViewOptions)
        binding.recycler.adapter = adapter
        binding.progress.visibility = View.GONE
        binding.recycler.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private fun handleTouches() {
        binding.back.setOnClickListener { finish() }
    }

    override fun showFullGif(gif: Gif) {
        val dialog = FullGifBottomSheet()
        val bundle = Bundle()
        bundle.putString("title", gif.title)
        bundle.putString("gifUrl", gif.gifUrl)
        bundle.putString("gid", gif.gid)
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "customDialog")
        currentGif = gif
    }

    override fun refreshFavoriteGifActivity() {
        setupRecyclerView()
    }

    override fun onFavoriteGifClick() {
        fun delete() {
            currentGif?.let {
                collection.document(it.gid)
                    .delete()
                    .addOnSuccessListener {
                        Logger.debugger("DocumentSnapshot successfully deleted!")
                        startActivity(Intent(this, FavoriteGifActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Logger.debugger("DocumentSnapshot successfully failed!")
                    }
            }
        }
        when (sharedPreferences.getBoolean(Saver.CONFIRM_DELETE, false)) {
            true -> {
                with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                    setTitle("Confirm delete")
                    setMessage("Are you sure about deleting?")
                    setPositiveButton("Yes") { _, _ ->
                        delete()
                    }
                    setNegativeButton("No") { _, _ ->

                    }
                    show()
                }
            }
            false -> delete()
        }
    }
}