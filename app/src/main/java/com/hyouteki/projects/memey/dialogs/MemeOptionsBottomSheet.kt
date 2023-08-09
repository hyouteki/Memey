package com.hyouteki.projects.memey.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.interfaces.Communicator

class MemeOptionsBottomSheet : BottomSheetDialogFragment() {
    private lateinit var comm: Communicator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_meme_options, container, false)
        val deleteButton = view.findViewById<ConstraintLayout>(R.id.delete)
        comm = activity as Communicator
        deleteButton.setOnClickListener {
            comm.deleteCurrentLocalMeme()
            dismiss()
        }
        return view
    }
}