package com.hyouteki.projects.memey.activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import com.hyouteki.projects.memey.adapters.MemeFireStoreAdapter
import com.hyouteki.projects.memey.comms.AdapterComms
import com.hyouteki.projects.memey.daos.MemeDao
import com.hyouteki.projects.memey.databinding.ActivityFavoriteMemeBinding
import com.hyouteki.projects.memey.dialogs.FullTitleBottomSheet
import com.hyouteki.projects.memey.interfaces.Logger
import com.hyouteki.projects.memey.interfaces.Saver
import com.hyouteki.projects.memey.interfaces.Tags
import com.hyouteki.projects.memey.models.Meme

class FavoriteMemeActivity : AppCompatActivity(), AdapterComms {
    private lateinit var binding: ActivityFavoriteMemeBinding
    private lateinit var adapter: MemeFireStoreAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private val user = FirebaseAuth.getInstance().currentUser!!
    private val collection = MemeDao().collection
    private val query = collection.whereEqualTo("uid", user.uid)
        .orderBy("mid", Query.Direction.DESCENDING)
    private val recyclerViewOptions = FirestoreRecyclerOptions
        .Builder<Meme>()
        .setQuery(query, Meme::class.java)
        .build()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteMemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.secBackground)
        sharedPreferences = Saver.getPreferences(this)
        setupRecyclerView()
        handleTouches()
    }

    private fun setupRecyclerView() {
        binding.progress.visibility = View.VISIBLE
        binding.recycler.visibility = View.INVISIBLE
        adapter = MemeFireStoreAdapter(this, recyclerViewOptions, true)
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

    override fun showFullMemeTitle(title: String) {
        val dialog = FullTitleBottomSheet()
        val bundle = Bundle()
        bundle.putString("title", title)
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "FullTitle#BottomSheet@Memey")
    }

    override fun onFavoriteMemeClick(meme: Meme) {
        fun delete() {
            collection.document(meme.mid)
                .delete()
                .addOnSuccessListener {
                    Logger.debugger("DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener {
                    Logger.debugger("DocumentSnapshot successfully failed!")
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

    override fun onShareMemeClick(meme: Meme) {
        val get = sharedPreferences.getString(Saver.WHAT_TO_SHARE, Tags.IMAGE_LINK)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        when (get) {
            Tags.IMAGE_LINK -> shareIntent.putExtra(Intent.EXTRA_TEXT, meme.photoUrl)
            else -> shareIntent.putExtra(Intent.EXTRA_TEXT, meme.postUrl)
        }
        startActivity(Intent.createChooser(shareIntent, "Send this amazing meme using..."))
    }

    override fun onRedirectMemeClick(meme: Meme) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(meme.postUrl)
        startActivity(openURL)
    }
}