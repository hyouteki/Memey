package com.hyouteki.projects.memey.viewmodels

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.daos.GifDao
import com.hyouteki.projects.memey.daos.LocalMemeDao
import com.hyouteki.projects.memey.daos.MemeDao
import com.hyouteki.projects.memey.daos.UserDao
import com.hyouteki.projects.memey.interfaces.Helper
import com.hyouteki.projects.memey.models.Meme

interface MemeyViewModel {
    companion object {
        private val gifCollection = GifDao().collection
        private val memeCollection = MemeDao().collection
        val localMemeCollection = LocalMemeDao.collection
        val userCollection = UserDao().collection
        fun removeGif(id: String) = gifCollection.document(id).delete()

        fun deleteAllMemes(context: Context) {
            val id = FirebaseAuth.getInstance().currentUser!!.uid
            memeCollection.whereEqualTo("uid", id).get().addOnSuccessListener { documents ->
                for (document in documents) {
                    memeCollection.document(document.id).delete()
                }
                Helper.makeToast(context, "Deleted successfully")
            }
        }

        fun deleteAllGifs(context: Context) {
            val id = FirebaseAuth.getInstance().currentUser!!.uid
            gifCollection.whereEqualTo("uid", id).get().addOnSuccessListener { documents ->
                for (document in documents) {
                    gifCollection.document(document.id).delete()
                }
                Helper.makeToast(context, "Deleted successfully")
            }
        }

        fun uploadMeme(meme: Meme) = LocalMemeDao.uploadMeme(meme)
        fun addMeme(meme: Meme) = MemeDao().addMeme(meme)
    }
}