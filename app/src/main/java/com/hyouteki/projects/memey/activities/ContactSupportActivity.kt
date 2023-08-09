package com.hyouteki.projects.memey.activities

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.daos.HelpDao
import com.hyouteki.projects.memey.databinding.ActivityContactSupportBinding
import com.hyouteki.projects.memey.interfaces.Helper
import com.hyouteki.projects.memey.interfaces.Saver
import com.hyouteki.projects.memey.models.Help

class ContactSupportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactSupportBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.background)
        initialize()
        handleTouches()
    }

    private fun initialize() {
        sharedPreferences = Saver.getPreferences(this)
        editor = Saver.getEditor(this)
        binding.title.setText(sharedPreferences.getString(Saver.CONTACT_SUPPORT_TITLE, ""))
        binding.description.setText(
            sharedPreferences.getString(
                Saver.CONTACT_SUPPORT_DESCRIPTION,
                ""
            )
        )
        if (binding.title.text.isNotEmpty() || binding.description.text.isNotEmpty()) {
            Helper.makeToast(this, "Loaded from draft")
        }
    }

    private fun handleTouches() {
        binding.save.setOnClickListener {
            editor.apply {
                putString(Saver.CONTACT_SUPPORT_TITLE, binding.title.text.toString())
                putString(Saver.CONTACT_SUPPORT_DESCRIPTION, binding.description.text.toString())
                apply()
            }
            Helper.makeToast(this, "Saved to draft successfully")
            finish()
        }
        binding.cancel.setOnClickListener { finish() }
        binding.submit.setOnClickListener {
            if (binding.title.text.isEmpty() || binding.description.text.isEmpty()) {
                Helper.makeToast(this, "Fill all the required details first")
                return@setOnClickListener
            }
            HelpDao().addHelp(
                Help(
                    FirebaseAuth.getInstance().currentUser?.uid!!,
                    System.currentTimeMillis().toString(),
                    binding.title.text.toString(),
                    binding.description.text.toString()
                )
            )
            editor.apply {
                putString(Saver.CONTACT_SUPPORT_TITLE, "")
                putString(Saver.CONTACT_SUPPORT_DESCRIPTION, "")
                apply()
            }
            Helper.makeToast(this, "Submitted successfully")
            finish()
        }
    }
}