package com.hyouteki.projects.memey.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.adapters.LocalMemeFireStoreAdapter
import com.hyouteki.projects.memey.databinding.ActivityMyMemesBinding
import com.hyouteki.projects.memey.dialogs.FullTitleBottomSheet
import com.hyouteki.projects.memey.dialogs.MemeOptionsBottomSheet
import com.hyouteki.projects.memey.interfaces.Communicator
import com.hyouteki.projects.memey.interfaces.Helper
import com.hyouteki.projects.memey.interfaces.Logger
import com.hyouteki.projects.memey.interfaces.Saver
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.viewmodels.MemeyViewModel

class MyMemesActivity : AppCompatActivity(), Communicator {
    private lateinit var binding: ActivityMyMemesBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val user = FirebaseAuth.getInstance().currentUser!!
    private lateinit var adapter: LocalMemeFireStoreAdapter
    private var currentLocalMeme: Meme? = null
    private val storageReference = FirebaseStorage.getInstance().reference
    private val query = MemeyViewModel
        .localMemeCollection
        .whereEqualTo("uid", user.uid)
        .orderBy("mid", Query.Direction.DESCENDING)
    private val recyclerViewOptions = FirestoreRecyclerOptions
        .Builder<Meme>()
        .setQuery(query, Meme::class.java)
        .build()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyMemesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.secBackground)

        initialize()
        handleTouches()
    }

    private fun handleTouches() {
        binding.back.setOnClickListener { finish() }
        binding.add.setOnClickListener {
            startActivity(Intent(this, AddMemeActivity::class.java))
        }
    }


    private fun setupRecyclerView() {
        adapter = LocalMemeFireStoreAdapter(this, recyclerViewOptions, true)
        binding.recycler.adapter = adapter
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

    private fun initialize() {
        setupRecyclerView()
        sharedPreferences = Saver.getPreferences(this)
    }


    override fun openMemeOptions(meme: Meme) {
        this.currentLocalMeme = meme
        MemeOptionsBottomSheet().show(supportFragmentManager, "MemeOptions#BottomSheet@Memey")
    }

    override fun shareMeme(meme: Meme) {
        val get = sharedPreferences.getString("shareLink", "Image link")
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        if (get == "Image link") {
            shareIntent.putExtra(Intent.EXTRA_TEXT, meme.photoUrl)
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, meme.postUrl)
        }
        startActivity(Intent.createChooser(shareIntent, "Send this amazing meme using..."))
    }

    override fun deleteCurrentLocalMeme() {
        currentLocalMeme?.let {
            MemeyViewModel.localMemeCollection.document(it.mid).delete().addOnSuccessListener {
                Helper.makeToast(this, "Deleted successfully")
            }.addOnFailureListener {
                Helper.makeToast(this, "Could not delete. Try again")
            }
        }
        currentLocalMeme?.let {
            val photoReference = storageReference.child("Meme/${it.mid}.png")
            photoReference.delete()
                .addOnSuccessListener { // File deleted successfully
                    Logger.debugger("onSuccess: deleted file")
                }.addOnFailureListener { // Uh-oh, an error occurred!
                    Logger.debugger("onFailure: did not delete file")
                }

        }
    }

    override fun showFullTitle(title: String) {
        Helper.makeToast(this, "hello")
        val dialog = FullTitleBottomSheet()
        val bundle = Bundle()
        bundle.putString("title", title)
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "FullTitle#BottomSheet@Memey")
    }
}