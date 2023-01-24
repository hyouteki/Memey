package com.hyouteki.projects.memey.classes

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage

class FirebaseStorageHandler {
    private val storageReference = FirebaseStorage.getInstance().reference
    private val TAG = "FirebaseStorageHandler"

    fun uploadImage(context: Context, imageUri: Uri) {
        val sharedPref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val uploadTime = System.currentTimeMillis()
        val uploadTask = storageReference
            .child("item_images/item_image$uploadTime.png")
            .putFile(imageUri)
        uploadTask.addOnSuccessListener {
            Log.i(TAG, "Image uploaded successfully")
            val downloadPathTask = storageReference
                .child("item_images/item_image$uploadTime.png")
                .downloadUrl
            downloadPathTask.addOnSuccessListener {
                Log.i(TAG, "Image downloaded successfully")
                Log.i(TAG, "$it")
                editor.apply {
                    putString("image_url", "$it")
                    apply()
                }
            }.addOnFailureListener {
                Log.i(TAG, "Image downloading failed")
            }
        }.addOnFailureListener {
            Log.i(TAG, "Image uploading failed")
        }
    }
}