package com.hyouteki.projects.memey.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.comms.AdapterComms
import com.hyouteki.projects.memey.interfaces.Communicator
import com.hyouteki.projects.memey.models.RandomMeme

class MemeAdapter(
    private val listener: AdapterComms
) : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    private val dataSet = arrayListOf<RandomMeme>()

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

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val item = dataSet[position]
        Glide.with(holder.memeImage.context)
            .load(item.photoUrl)
            .into(holder.memeImage)
        holder.memeTitle.text = item.title
        holder.upvoteCount.text = item.upvoteCount
        holder.favoriteButton.setImageResource(R.drawable.ic_favorite)
        holder.favoriteButton.setOnClickListener {
            if (item.liked) {
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite)
            } else {
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
            }
            this.listener.onFavoriteMemeClick(item)
        }
        holder.memeTitle.setOnLongClickListener {
            listener.showFullMemeTitle(item.title)
            true
        }
        holder.shareButton.setOnClickListener {
            this.listener.onShareMemeClick(item)
        }
        holder.redirectButton.setOnClickListener {
            this.listener.onRedirectMemeClick(item)
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateData(newDataSet: List<RandomMeme>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }
}