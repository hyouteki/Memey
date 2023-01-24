package com.hyouteki.projects.memey.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.fragments.FavMemeFragment
import com.hyouteki.projects.memey.models.Meme

class MemeFireStoreAdapter(
    private val listener: FavMemeFragment,
    options: FirestoreRecyclerOptions<Meme>
) :
    FirestoreRecyclerAdapter<Meme, MemeFireStoreAdapter.MemeViewHolder>(options) {
    inner class MemeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val memeImage: ImageView = view.findViewById(R.id.iv_mli_meme_image)
        val memeTitle: TextView = view.findViewById(R.id.tv_mli_title_text)
        val favoriteButton: ImageView = view.findViewById(R.id.btn_mli_favorite)
        val shareButton: ImageView = view.findViewById(R.id.btn_mli_share)
        val redirectButton: ImageView = view.findViewById(R.id.btn_mli_redirect)
        val upvoteCount: TextView = view.findViewById(R.id.tv_mli_upvote_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        return MemeViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.meme_li, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int, model: Meme) {
        Glide.with(holder.memeImage.context)
            .load(model.photoUrl)
            .into(holder.memeImage)
        holder.memeTitle.text = model.title
        holder.upvoteCount.text = model.upvoteCount
        holder.favoriteButton.setOnClickListener {
            this.listener.favoriteButtonClickMethod(model)
        }
        holder.shareButton.setOnClickListener {
            this.listener.shareButtonClickMethod(model)
        }
        holder.redirectButton.setOnClickListener {
            this.listener.redirectButtonClickMethod(model)
        }
    }
}