package com.hyouteki.projects.memey.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.comms.BottomSheetComms
import com.hyouteki.projects.memey.daos.GifDao
import com.hyouteki.projects.memey.models.Gif

class FullGifBottomSheet : BottomSheetDialogFragment() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var comm: BottomSheetComms

    // for correct dialog size
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1.
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_full_gif, container, false)
        val title = view.findViewById<TextView>(R.id.title)
        val gif = view.findViewById<ImageView>(R.id.gif)
        val favoriteButton = view.findViewById<ImageView>(R.id.favorite)
        val shareButton = view.findViewById<ImageView>(R.id.share)

        val titleText = arguments?.getString("title")
        val gifUrl = arguments?.getString("gifUrl")
        val gid = arguments?.getString("gid")

        var liked = false
        var fav = false

        comm = activity as BottomSheetComms

        title.text = titleText
        Glide.with(gif.context).load(gifUrl).into(gif)

        gid?.let {
            favoriteButton.setImageResource(R.drawable.ic_delete_all)
        }
        favoriteButton.setOnClickListener {
            gid?.let {
                comm.onFavoriteGifClick()
                dismiss()
            } ?: run {
                if (fav) {
                    fav = false
                    favoriteButton.setImageResource(R.drawable.ic_favorite)
                } else {
                    fav = true
                    favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
                }
                if (!liked) {
                    liked = true
                    val currentTime = System.currentTimeMillis()
                    val gif = Gif(
                        currentUser?.uid!!,
                        currentTime.toString(),
                        gifUrl.toString(),
                        titleText.toString()
                    )
                    GifDao().addGif(gif)
                }
            }
        }
        shareButton.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, gifUrl)
            startActivity(Intent.createChooser(shareIntent, "Send this amazing gif using..."))
        }

        return view
    }
}