package com.hyouteki.projects.memey.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.databinding.ActivityAddMemeBinding
import com.hyouteki.projects.memey.dialogs.ProgressDialog
import com.hyouteki.projects.memey.interfaces.Helper
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.viewmodels.MemeyViewModel
import kotlinx.coroutines.*

class AddMemeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMemeBinding
    private var imageUri: Uri? = null
    private val dialog = ProgressDialog()
    private val storageReference = FirebaseStorage.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    companion object {
        private const val IMAGE_REQUEST_CODE = 2
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.background)

        binding.addMeme.setOnClickListener { requestImage() }
        binding.memeImageCard.setOnClickListener { requestImage() }

        binding.cancel.setOnClickListener { finish() }

        binding.submit.setOnClickListener {
            if (binding.title.text.toString() == "") {
                Helper.makeToast(this, "Add suitable title to your meme")
                return@setOnClickListener
            } else if (imageUri == null) {
                Helper.makeToast(this, "Add image to your meme")
                return@setOnClickListener
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    dialog.show(supportFragmentManager, "Progress#Dialog@Memey")
                    withContext(Dispatchers.Main) {
                        handleSubmit()
                    }
                }
            }
        }
    }

    private fun requestImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            binding.memeImage.setImageURI(data?.data)
            binding.addMeme.visibility = View.GONE
            this.imageUri = data?.data
        }
    }

    private suspend fun handleSubmit() {
        val currentTime = System.currentTimeMillis()
        val uploadTask = storageReference.child("Meme/$currentTime.png").putFile(imageUri!!)
        uploadTask.addOnSuccessListener {
            val downloadTask = storageReference.child("Meme/$currentTime.png").downloadUrl
            downloadTask.addOnSuccessListener {
                val meme = Meme(
                    uid = currentUser.uid,
                    mid = currentTime.toString(),
                    photoUrl = it.toString(),
                    postUrl = "null",
                    title = binding.title.text.toString(),
                    nsfw = binding.nsfwSwitch.isChecked,
                    upvoteCount = "null"
                )
                MemeyViewModel.uploadMeme(meme)
                finish()
            }.addOnFailureListener {
                Helper.makeToast(this, "Image downloading failed")
                dialog.dismiss()
            }
        }.addOnFailureListener {
            Helper.makeToast(this, "Image uploading failed")
            dialog.dismiss()
        }
    }
}