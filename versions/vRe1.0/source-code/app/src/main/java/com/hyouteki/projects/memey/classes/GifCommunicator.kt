package com.hyouteki.projects.memey.classes

import com.hyouteki.projects.memey.models.GifMvvm

interface GifCommunicator {
    fun openGif(gif: GifMvvm)
}