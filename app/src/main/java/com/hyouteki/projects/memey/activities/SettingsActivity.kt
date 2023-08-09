package com.hyouteki.projects.memey.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.databinding.ActivitySettingsBinding
import com.hyouteki.projects.memey.interfaces.Helper
import com.hyouteki.projects.memey.interfaces.Saver
import com.hyouteki.projects.memey.interfaces.Tags

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.secBackground)
        initialize()
        handleTouches()
        handleDialogs()
    }

    @SuppressLint("SetTextI18n")
    private fun initialize() {
        sharedPreferences = Saver.getPreferences(this)
        editor = Saver.getEditor(this)

        binding.confirmDeleteSwitch.isChecked =
            sharedPreferences.getBoolean(Saver.CONFIRM_DELETE, false)
        binding.defaultTabDesc.text =
            sharedPreferences.getString(Saver.DEFAULT_TAB, Tags.MEME_TAB)
        binding.memeTabLayoutDesc.text =
            sharedPreferences.getString(Saver.MEME_TAB_LAYOUT, Tags.MULTIPLE_MEME_LAYOUT)
        when (sharedPreferences.getString(Saver.MEME_TAB_LAYOUT, Tags.MULTIPLE_MEME_LAYOUT)) {
            Tags.MULTIPLE_MEME_LAYOUT -> {
                binding.memeCount.visibility = View.VISIBLE
                binding.swipeLeftGesture.visibility = View.GONE
                binding.swipeRightGesture.visibility = View.GONE
            }
            else -> {
                binding.memeCount.visibility = View.GONE
                binding.swipeLeftGesture.visibility = View.VISIBLE
                binding.swipeRightGesture.visibility = View.VISIBLE
            }
        }
        binding.nsfwSwitch.isChecked =
            sharedPreferences.getBoolean(Saver.NSFW_FILTER, false)
        binding.whatToShareDesc.text =
            sharedPreferences.getString(Saver.WHAT_TO_SHARE, Tags.IMAGE_LINK)
        binding.memeCountDesc.text = "Memes: ${
            sharedPreferences.getInt(Saver.MEME_COUNT, 10)
        }"
        binding.swipeRightGestureDesc.text =
            sharedPreferences.getString(Saver.SWIPE_RIGHT_GESTURE, Tags.SHARE_MEME)
        binding.swipeLeftGestureDesc.text =
            sharedPreferences.getString(Saver.SWIPE_LEFT_GESTURE, Tags.FAVORITE_MEME)
    }

    private fun handleTouches() {
        binding.back.setOnClickListener { finish() }
        binding.confirmDelete.setOnClickListener {
            binding.confirmDeleteSwitch.isChecked = !binding.confirmDeleteSwitch.isChecked
            editor.apply {
                putBoolean(Saver.CONFIRM_DELETE, binding.confirmDeleteSwitch.isChecked)
                apply()
            }
        }
        binding.nsfwFilter.setOnClickListener {
            binding.nsfwSwitch.isChecked = !binding.nsfwSwitch.isChecked
            editor.apply {
                putBoolean(Saver.NSFW_FILTER, binding.nsfwSwitch.isChecked)
                apply()
            }
        }
        binding.confirmDeleteSwitch.setOnClickListener {
            editor.apply {
                putBoolean(Saver.CONFIRM_DELETE, binding.confirmDeleteSwitch.isChecked)
                apply()
            }
        }
        binding.nsfwSwitch.setOnClickListener {
            editor.apply {
                putBoolean(Saver.NSFW_FILTER, binding.nsfwSwitch.isChecked)
                apply()
            }
        }
    }

    private fun handleDialogs() {
        binding.defaultTab.setOnClickListener {
            with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                setTitle("Default tab")
                val items = arrayOf(Tags.MEME_TAB, Tags.GIF_TAB, Tags.MEMEY_MEME_TAB)
                var checkedItem =
                    when (sharedPreferences.getString(Saver.DEFAULT_TAB, Tags.MEME_TAB)) {
                        Tags.MEME_TAB -> 0
                        Tags.GIF_TAB -> 1
                        else -> 2
                    }
                setSingleChoiceItems(items, checkedItem,
                    DialogInterface.OnClickListener { _, which ->
                        checkedItem = which
                    })
                setPositiveButton("Save") { _, _ ->
                    editor.apply {
                        putString(Saver.DEFAULT_TAB, items[checkedItem])
                        apply()
                    }
                    initialize()
                }
                setNegativeButton("Cancel") { _, _ ->

                }
                show()
            }
        }
        binding.memeTabLayout.setOnClickListener {
            with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                setTitle("Meme tab layout")
                val items = arrayOf(Tags.MULTIPLE_MEME_LAYOUT, Tags.SINGLE_MEME_LAYOUT)
                var checkedItem =
                    when (sharedPreferences.getString(
                        Saver.MEME_TAB_LAYOUT,
                        Tags.MULTIPLE_MEME_LAYOUT
                    )) {
                        Tags.MULTIPLE_MEME_LAYOUT -> 0
                        else -> 1
                    }
                setSingleChoiceItems(items, checkedItem,
                    DialogInterface.OnClickListener { _, which ->
                        checkedItem = which
                    })
                setPositiveButton("Save") { _, _ ->
                    editor.apply {
                        putString(Saver.MEME_TAB_LAYOUT, items[checkedItem])
                        apply()
                    }
                    initialize()
                }
                setNegativeButton("Cancel") { _, _ ->

                }
                show()
            }
        }
        binding.whatToShare.setOnClickListener {
            with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                setTitle("What to share")
                val items = arrayOf(Tags.IMAGE_LINK, Tags.POST_LINK)
                var checkedItem =
                    when (sharedPreferences.getString(
                        Saver.WHAT_TO_SHARE,
                        Tags.IMAGE_LINK
                    )) {
                        Tags.IMAGE_LINK -> 0
                        else -> 1
                    }
                setSingleChoiceItems(items, checkedItem,
                    DialogInterface.OnClickListener { _, which ->
                        checkedItem = which
                    })
                setPositiveButton("Save") { _, _ ->
                    editor.apply {
                        editor.putString(Saver.WHAT_TO_SHARE, items[checkedItem])
                        apply()
                    }
                    initialize()
                }
                setNegativeButton("Cancel") { _, _ ->

                }
                show()
            }
        }
        binding.memeCount.setOnClickListener {
            when (sharedPreferences.getString(Saver.MEME_TAB_LAYOUT, Tags.MULTIPLE_MEME_LAYOUT)) {
                Tags.MULTIPLE_MEME_LAYOUT -> {
                    with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                        setTitle("Memes per page")
                        val view: View = layoutInflater.inflate(R.layout.meme_count_picker, null)
                        setView(view)
                        val numberPicker: NumberPicker = view.findViewById(R.id.meme_count_picker)
                        numberPicker.maxValue = 50
                        numberPicker.minValue = 1
                        numberPicker.wrapSelectorWheel = false
                        numberPicker.value = sharedPreferences.getInt(Saver.MEME_COUNT, 10)
                        setPositiveButton("Save") { _, _ ->
                            editor.apply {
                                editor.putInt(Saver.MEME_COUNT, numberPicker.value)
                                apply()
                            }
                            initialize()
                        }
                        setNegativeButton("Cancel") { _, _ ->

                        }
                        show()
                    }
                }
                Tags.SINGLE_MEME_LAYOUT -> {
                    Helper.makeToast(this, "Only available in multiple meme layout")
                }
            }
        }
        binding.swipeRightGesture.setOnClickListener {
            with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                setTitle("Choose swipe right gesture")
                val items = arrayOf(Tags.FAVORITE_MEME, Tags.REDIRECT_MEME, Tags.SHARE_MEME)
                var checkedItem =
                    when (sharedPreferences.getString(
                        Saver.SWIPE_RIGHT_GESTURE,
                        Tags.SHARE_MEME
                    )) {
                        Tags.FAVORITE_MEME -> 0
                        Tags.REDIRECT_MEME -> 1
                        else -> 2
                    }
                setSingleChoiceItems(items, checkedItem,
                    DialogInterface.OnClickListener { _, which ->
                        checkedItem = which
                    })
                setPositiveButton("Save") { _, _ ->
                    editor.apply {
                        putString(Saver.SWIPE_RIGHT_GESTURE, items[checkedItem])
                        apply()
                    }
                    initialize()
                }
                setNegativeButton("Cancel") { _, _ ->

                }
                show()
            }
        }
        binding.swipeLeftGesture.setOnClickListener {
            with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                setTitle("Choose swipe left gesture")
                val items = arrayOf(Tags.FAVORITE_MEME, Tags.REDIRECT_MEME, Tags.SHARE_MEME)
                var checkedItem =
                    when (sharedPreferences.getString(
                        Saver.SWIPE_LEFT_GESTURE,
                        Tags.FAVORITE_MEME
                    )) {
                        Tags.FAVORITE_MEME -> 0
                        Tags.REDIRECT_MEME -> 1
                        else -> 2
                    }
                setSingleChoiceItems(items, checkedItem,
                    DialogInterface.OnClickListener { _, which ->
                        checkedItem = which
                    })
                setPositiveButton("Save") { _, _ ->
                    editor.apply {
                        putString(Saver.SWIPE_LEFT_GESTURE, items[checkedItem])
                        apply()
                    }
                    initialize()
                }
                setNegativeButton("Cancel") { _, _ ->

                }
                show()
            }
        }
    }
}