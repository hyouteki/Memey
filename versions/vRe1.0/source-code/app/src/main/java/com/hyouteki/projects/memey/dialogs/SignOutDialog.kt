package com.hyouteki.projects.memey.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hyouteki.projects.memey.R

class SignOutDialog : DialogFragment() {

    // for correct dialog size
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1.
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_sign_out, container, false)
        val yes = view.findViewById<Button>(R.id.btn_dso_yes)
        val no = view.findViewById<Button>(R.id.btn_dso_no)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val gsc = GoogleSignIn.getClient(requireActivity(), gso)
        val auth = Firebase.auth

        no.setOnClickListener {
            dismiss()
        }

        yes.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            auth.signOut()
            gsc.signOut()
            val broadcastIntent = Intent()
            broadcastIntent.action = "com.package.ACTION_LOGOUT"
            activity?.sendBroadcast(broadcastIntent)
            dismiss()
        }

        return view
    }

}