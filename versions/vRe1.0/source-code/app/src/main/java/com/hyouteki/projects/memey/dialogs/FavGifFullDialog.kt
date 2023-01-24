package com.hyouteki.projects.memey.dialogs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.classes.Frag2ActCommunicator
import com.hyouteki.projects.memey.daos.GifDao
import com.hyouteki.projects.memey.fragments.FavMemeFragment
import com.hyouteki.projects.memey.models.Gif

class FavFullGifDialog : DialogFragment() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var comm: Frag2ActCommunicator

    companion object {
        private const val TAG = "FavGifFullDialog"
    }

    // for correct dialog size
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1.
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_full_gif, container, false)
        val title = view.findViewById<TextView>(R.id.tv_dfg_title)
        val gif = view.findViewById<ImageView>(R.id.iv_dfg_gif)
        val favoriteButton = view.findViewById<ImageView>(R.id.btn_dfg_favorite)
        val shareButton = view.findViewById<ImageView>(R.id.btn_dfg_share)
        val closeButton = view.findViewById<ImageView>(R.id.btn_dfg_close)

        val titleText = arguments?.getString("title")
        val gifUrl = arguments?.getString("gifUrl")
        val gid = arguments?.getString("gid")

        comm = activity as Frag2ActCommunicator

        favoriteButton.setImageResource(R.drawable.ic_favorite_filled)

        title.text = titleText
        Glide.with(gif.context).load(gifUrl).into(gif)

        closeButton.setOnClickListener {
            dismiss()
        }

        favoriteButton.setOnClickListener {
            if (gid != null) {
                GifDao().collection.document(gid)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!")
                        comm.refreshFavGifFragment()
                        dismiss()
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "DocumentSnapshot successfully failed!")
                    }
            }
        }
        shareButton.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, gifUrl);
            startActivity(Intent.createChooser(shareIntent, "Send this amazing gif using..."))
        }

        return view
    }
}