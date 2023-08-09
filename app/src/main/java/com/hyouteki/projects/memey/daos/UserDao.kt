@file:OptIn(DelicateCoroutinesApi::class)

package com.hyouteki.projects.memey.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.hyouteki.projects.memey.models.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserDao {

    private val database = FirebaseFirestore.getInstance()
    val collection = database.collection("User")

    fun addUser(user: User?) {
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                collection.document(user.uid).set(it)
            }
        }
    }

    fun getUser(uid: String) = collection.document(uid).get()
}