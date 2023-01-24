package com.hyouteki.projects.memey.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.models.RandomMeme

class MemeAdapter(
    private val listener: Communicator
) : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    private val dataSet = arrayListOf<RandomMeme>()

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
            this.listener.favoriteButtonClickMethod(item)
        }
        holder.shareButton.setOnClickListener {
            this.listener.shareButtonClickMethod(item)
        }
        holder.redirectButton.setOnClickListener {
            this.listener.redirectButtonClickMethod(item)
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateData(newDataSet: List<RandomMeme>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }
}