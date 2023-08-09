package com.hyouteki.projects.memey.interfaces

import android.util.Log

interface Logger {
    companion object {
        private const val TAG = "Logger@Memey"

        fun debugger(message: String) {
            Log.d(TAG, message)
        }
    }
}