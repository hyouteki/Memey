package com.hyouteki.projects.memey.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.daos.GifDao
import com.hyouteki.projects.memey.daos.MemeDao

class ConfirmDialog : DialogFragment() {
    private val currentUser = FirebaseAuth.getInstance().currentUser

    // for correct dialog size
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1.
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_confirm, container, false)
        val yes = view.findViewById<Button>(R.id.btn_dc_yes)
        val no = view.findViewById<Button>(R.id.btn_dc_no)

        val deleteWhat = arguments?.getString("deleteWhat")

        no.setOnClickListener {
            dismiss()
        }

        yes.setOnClickListener {
            when (deleteWhat) {
                "meme" -> {
                    MemeDao().collection.whereEqualTo("uid", currentUser?.uid!!).get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                MemeDao().collection.document(document.id).delete()
                            }
                        }
                    dismiss()
                }
                "gif" -> {
                    GifDao().collection.whereEqualTo("uid", currentUser?.uid!!).get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                GifDao().collection.document(document.id).delete()
                            }
                        }
                    dismiss()
                }
                else -> {

                }
            }
        }

        return view
    }
}