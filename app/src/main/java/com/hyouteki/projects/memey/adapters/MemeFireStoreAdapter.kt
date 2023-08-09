package com.hyouteki.projects.memey.adapters

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
import com.hyouteki.projects.memey.comms.AdapterComms
import com.hyouteki.projects.memey.models.Meme

class MemeFireStoreAdapter(
    private val listener: AdapterComms,
    options: FirestoreRecyclerOptions<Meme>,
    private val isFavorite: Boolean = false
) :
    FirestoreRecyclerAdapter<Meme, MemeFireStoreAdapter.MemeViewHolder>(options) {
    inner class MemeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val memeImage: ImageView = view.findViewById(R.id.meme_image)
        val memeTitle: TextView = view.findViewById(R.id.title)
        val favoriteButton: ImageView = view.findViewById(R.id.favorite)
        val shareButton: ImageView = view.findViewById(R.id.share)
        val redirectButton: ImageView = view.findViewById(R.id.redirect)
        val upvoteCount: TextView = view.findViewById(R.id.upvote_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        return MemeViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.meme_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int, model: Meme) {
        Glide.with(holder.memeImage.context).load(model.photoUrl)
//            .thumbnail(0.5f)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.memeImage)
        holder.memeTitle.text = model.title
        holder.upvoteCount.text = model.upvoteCount
        if (isFavorite) {
            holder.favoriteButton.setImageResource(R.drawable.ic_delete_all)
        }
        holder.memeTitle.setOnLongClickListener {
            listener.showFullMemeTitle(model.title)
            true
        }
        holder.favoriteButton.setOnClickListener {
            this.listener.onFavoriteMemeClick(model)
        }
        holder.shareButton.setOnClickListener {
            this.listener.onShareMemeClick(model)
        }
        holder.redirectButton.setOnClickListener {
            this.listener.onRedirectMemeClick(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}