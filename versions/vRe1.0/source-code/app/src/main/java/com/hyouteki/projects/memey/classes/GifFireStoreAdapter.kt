package com.hyouteki.projects.memey.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.fragments.FavGifFragment
import com.hyouteki.projects.memey.models.Gif

class GifFireStoreAdapter(
    private val listener: FavGifFragment,
    options: FirestoreRecyclerOptions<Gif>
) :
    FirestoreRecyclerAdapter<Gif, GifFireStoreAdapter.GifViewHolder>(options) {
    inner class GifViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gif: ImageView = view.findViewById(R.id.iv_gli_gif)
        val card: CardView = view.findViewById(R.id.cv_gli_gif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        return GifViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.gif_li, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int, model: Gif) {
        Glide.with(holder.gif.context)
            .load(model.gifUrl)
            .into(holder.gif)
        holder.card.setOnClickListener {
            this.listener.openGif(model)
        }
    }
}