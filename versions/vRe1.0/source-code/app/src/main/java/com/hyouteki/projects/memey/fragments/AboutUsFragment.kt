package com.hyouteki.projects.memey.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.hyouteki.projects.memey.R

class AboutUsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about_us, container, false)
        val githubButton = view.findViewById<CardView>(R.id.cv_fau_github)
        val instagramButton = view.findViewById<CardView>(R.id.cv_fau_instagram)
        val linkedInButton = view.findViewById<CardView>(R.id.cv_fau_linked_in)
        githubButton.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://github.com/Hyouteki")
            startActivity(openURL)
        }
        instagramButton.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://www.instagram.com/mainlakshayhoon/")
            startActivity(openURL)
        }
        linkedInButton.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://www.linkedin.com/in/lakshay-chauhan-319200239/")
            startActivity(openURL)
        }
        return view
    }
}