package com.hyouteki.projects.memey.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyouteki.projects.memey.comms.DialogMainComms
import com.hyouteki.projects.memey.databinding.BottomSheetSwitchBinding

class SwitchBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetSwitchBinding
    private lateinit var main: DialogMainComms

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomSheetSwitchBinding.inflate(inflater, container, false)
        main = activity as DialogMainComms
        handleTouches()
        return binding.root
    }

    private fun handleTouches() {
        binding.meme.setOnClickListener {
            main.switchToMeme()
            dismiss()
        }
        binding.gif.setOnClickListener {
            main.switchToGif()
            dismiss()
        }
        binding.localMeme.setOnClickListener {
            main.switchToLocalMeme()
            dismiss()
        }
    }
}