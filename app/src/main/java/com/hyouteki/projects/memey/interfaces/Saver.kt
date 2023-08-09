package com.hyouteki.projects.memey.interfaces

import android.content.Context
import android.content.SharedPreferences

interface Saver {

    companion object {
        private const val TAG = "SharedPreferences@Memey"
        private const val MODE = Context.MODE_PRIVATE

        // TAG names
        const val DEFAULT_TAB = "DefaultTab"
        const val CONFIRM_DELETE = "ConfirmDelete"
        const val MEME_TAB_LAYOUT = "MemeTabLayout"
        const val NSFW_FILTER = "NSFWFilter"
        const val WHAT_TO_SHARE = "WhatToShare"
        const val MEME_COUNT = "MemeCount"
        const val SWIPE_RIGHT_GESTURE = "SwipeRightGesture"
        const val SWIPE_LEFT_GESTURE = "SwipeLeftGesture"
        const val CONTACT_SUPPORT_TITLE = "ContactSupportTitle"
        const val CONTACT_SUPPORT_DESCRIPTION = "ContactSupportDescription"

        fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(TAG, MODE)
        }

        fun getEditor(context: Context): SharedPreferences.Editor {
            return getPreferences(context).edit()
        }
    }
}