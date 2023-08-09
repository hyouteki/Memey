package com.hyouteki.projects.memey.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.comms.AdapterComms
import com.hyouteki.projects.memey.models.GifMvvm

class GifAdapter(private val listener: AdapterComms) :
    RecyclerView.Adapter<GifAdapter.GifViewHolder>() {

    private val dataSet = arrayListOf<GifMvvm>()

    inner class GifViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gif: ImageView = view.findViewById(R.id.iv_gli_gif)
        val card: CardView = view.findViewById(R.id.cv_gli_gif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.gif_list_item, parent, false)
        return GifViewHolder(view)
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val item = dataSet[position]
        Glide.with(holder.gif.context).load(item.gifUrl).into(holder.gif)
        holder.card.setOnClickListener {
            listener.showFullGif(item)
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateData(newDataSet: ArrayList<GifMvvm>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }
}