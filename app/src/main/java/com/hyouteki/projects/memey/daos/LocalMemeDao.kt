package com.hyouteki.projects.memey.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.hyouteki.projects.memey.models.Meme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocalMemeDao {
    companion object {
        val collection = FirebaseFirestore.getInstance()
            .collection("LocalMeme")

        fun uploadMeme(meme: Meme?) {
            GlobalScope.launch {
                meme?.let {
                    collection.document(it.mid).set(it)
                }
            }
        }

    }

}