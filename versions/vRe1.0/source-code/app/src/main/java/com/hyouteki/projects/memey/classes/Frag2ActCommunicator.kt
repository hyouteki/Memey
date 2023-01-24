package com.hyouteki.projects.memey.classes

import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.models.RandomMeme

interface Frag2ActCommunicator {
    fun refreshFavMemeFragment()
    fun refreshFavGifFragment()
    fun shareMeme(meme: Meme)
    fun shareMeme(meme: RandomMeme)
}