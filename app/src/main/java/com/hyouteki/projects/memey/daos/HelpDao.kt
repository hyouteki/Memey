@file:OptIn(DelicateCoroutinesApi::class)

package com.hyouteki.projects.memey.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.hyouteki.projects.memey.models.Help
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HelpDao {

    private val database = FirebaseFirestore.getInstance()
    val collection = database.collection("Help")

    fun addHelp(help: Help?) {
        help?.let {
            GlobalScope.launch(Dispatchers.IO) {
                collection.document(help.hid).set(it)
            }
        }
    }

    fun getHelp(hid: String) = collection.document(hid).get()
}