package com.hyouteki.projects.memey.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.dialogs.ConfirmDialog

class SettingsFragment : Fragment() {
    private lateinit var tabSetting: AutoCompleteTextView
    private lateinit var shareLinkSetting: AutoCompleteTextView
    private lateinit var memeCountPicker: NumberPicker
    private lateinit var deleteAllMeme: ImageView
    private lateinit var deleteAllGif: ImageView
    private lateinit var nsfwSwitch: SwitchMaterial
    private lateinit var sharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val save = view.findViewById<Button>(R.id.btn_fs_save)
        val cancel = view.findViewById<Button>(R.id.btn_fs_cancel)
        nsfwSwitch = view.findViewById(R.id.sm_fs_nsfw)
        tabSetting = view.findViewById(R.id.actv_fs_tabSetting)
        memeCountPicker = view.findViewById(R.id.np_fs_memeCount)
        shareLinkSetting = view.findViewById(R.id.actv_fs_shareLink)
        deleteAllMeme = view.findViewById(R.id.iv_fs_delete_all_meme)
        deleteAllGif = view.findViewById(R.id.iv_fs_delete_all_gif)

        sharedPref = requireActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        onLaunch()

        deleteAllMeme.setOnClickListener {
            val dialog = ConfirmDialog()
            val bundle = Bundle()
            bundle.putString("deleteWhat", "meme")
            dialog.arguments = bundle
            dialog.show(childFragmentManager, "customDialog")
        }

        deleteAllGif.setOnClickListener {
            val dialog = ConfirmDialog()
            val bundle = Bundle()
            bundle.putString("deleteWhat", "gif")
            dialog.arguments = bundle
            dialog.show(childFragmentManager, "customDialog")
        }

        cancel.setOnClickListener { onLaunch() }

        save.setOnClickListener {
            editor?.apply {
                editor.putString("currentTab", tabSetting.text.toString())
                editor.putBoolean("nsfwFilter", nsfwSwitch.isChecked)
                editor.putString("shareLink", shareLinkSetting.text.toString())
                editor.putInt("memeCount", memeCountPicker.value)
                apply()
            }
            Toast.makeText(requireActivity(), "Settings saved", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun onLaunch() {
        tabSetting.setText(sharedPref.getString("currentTab", "Meme"))
        shareLinkSetting.setText(sharedPref.getString("shareLink", "Image link"))
        nsfwSwitch.isChecked = sharedPref.getBoolean("nsfwFilter", false)
        memeCountPicker.minValue = 1
        memeCountPicker.maxValue = 50
        memeCountPicker.value = sharedPref.getInt("memeCount", 10)
        onResume()
    }

    override fun onResume() {
        super.onResume()
        // Tab Settings
        val tabs = resources.getStringArray(R.array.tabs)
        var arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_li, tabs)
        tabSetting.setAdapter(arrayAdapter)
        val shareLinks = resources.getStringArray(R.array.share_link)
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_li, shareLinks)
        shareLinkSetting.setAdapter(arrayAdapter)
    }
}