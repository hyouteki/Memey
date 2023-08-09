package com.hyouteki.projects.memey.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.interfaces.Communicator
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.models.User
import com.hyouteki.projects.memey.viewmodels.MemeyViewModel
import de.hdodenhof.circleimageview.CircleImageView

class LocalMemeFireStoreAdapter(
    private val listener: Communicator,
    options: FirestoreRecyclerOptions<Meme>,
    private val myMemes: Boolean = false
) :
    FirestoreRecyclerAdapter<Meme, LocalMemeFireStoreAdapter.LocalMemeViewHolder>(options) {
    inner class LocalMemeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val meme: CardView = view.findViewById(R.id.meme_card)
        val userImage: CircleImageView = view.findViewById(R.id.user_image)
        val userName: TextView = view.findViewById(R.id.user_name)
        val memeImage: ImageView = view.findViewById(R.id.meme_image)
        val memeTitle: TextView = view.findViewById(R.id.title)
        val shareButton: ImageView = view.findViewById(R.id.share)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalMemeViewHolder {
        return LocalMemeViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.local_meme_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LocalMemeViewHolder, position: Int, model: Meme) {
        MemeyViewModel
            .userCollection
            .document(model.uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.toObject(User::class.java)?.let { user ->
                    holder.userName.text = user.displayName
                    Glide.with(holder.userImage.context)
                        .load(user.photoUrl)
                        .circleCrop()
                        .dontAnimate()
                        .placeholder(R.drawable.ic_person)
                        .into(holder.userImage)
                }
            }
        holder.memeTitle.text = model.title
        Glide.with(holder.memeImage.context)
            .load(model.photoUrl)
            .into(holder.memeImage)
        if (myMemes) {
            holder.meme.setOnLongClickListener {
                listener.openMemeOptions(model)
                true
            }
        }
        holder.shareButton.setOnClickListener {
            this.listener.shareMeme(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}