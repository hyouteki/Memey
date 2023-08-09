@file:OptIn(DelicateCoroutinesApi::class)

package com.hyouteki.projects.memey.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.hyouteki.projects.memey.models.Meme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MemeDao {

    private val database = FirebaseFirestore.getInstance()
    val collection = database.collection("Meme")

    fun addMeme(meme: Meme?) {
        meme?.let {
            GlobalScope.launch(Dispatchers.IO) {
                collection.document(meme.mid).set(it)
            }
        }
    }

    fun getMeme(mid: String) = collection.document(mid).get()
}