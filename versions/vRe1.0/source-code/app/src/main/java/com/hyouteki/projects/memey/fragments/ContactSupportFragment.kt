package com.hyouteki.projects.memey.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.daos.HelpDao
import com.hyouteki.projects.memey.models.Help

class ContactSupportFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_support, container, false)
        val title = view.findViewById<TextInputEditText>(R.id.tiet_fcs_title)
        val desc = view.findViewById<TextInputEditText>(R.id.tiet_fcs_desc)
        val submitButton = view.findViewById<Button>(R.id.btn_fcs_submit)

        submitButton.setOnClickListener {
            if (title.text.toString() == "" || desc.text.toString() == "") {
                Toast.makeText(requireContext(), "Fill all the required fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                HelpDao().addHelp(
                    Help(
                        FirebaseAuth.getInstance().currentUser?.uid!!,
                        System.currentTimeMillis().toString(),
                        title.text.toString(),
                        desc.text.toString()
                    )
                )
                title.setText("")
                desc.setText("")
                Toast.makeText(requireContext(), "Submitted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }
}