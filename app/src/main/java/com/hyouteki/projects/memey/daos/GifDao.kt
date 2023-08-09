@file:OptIn(DelicateCoroutinesApi::class)

package com.hyouteki.projects.memey.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.hyouteki.projects.memey.models.Gif
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GifDao {

    private val database = FirebaseFirestore.getInstance()
    val collection = database.collection("Gif")

    fun addGif(gif: Gif?) {
        gif?.let {
            GlobalScope.launch(Dispatchers.IO) {
                collection.document(gif.gid).set(it)
            }
        }
    }

    fun getGif(gid: String) = collection.document(gid).get()
}