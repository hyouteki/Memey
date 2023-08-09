package com.hyouteki.projects.memey.comms

import com.hyouteki.projects.memey.models.Gif
import com.hyouteki.projects.memey.models.GifMvvm
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.models.RandomMeme

interface AdapterComms {
    // MemeFireStoreAdapter
    fun showFullMemeTitle(title: String) {}
    fun onFavoriteMemeClick(meme: Meme) {}
    fun onShareMemeClick(meme: Meme) {}
    fun onRedirectMemeClick(meme: Meme) {}
    fun onFavoriteMemeClick(meme: RandomMeme) {}
    fun onShareMemeClick(meme: RandomMeme) {}
    fun onRedirectMemeClick(meme: RandomMeme) {}

    // GifFireStoreAdapter
    fun showFullGif(gif: Gif) {}
    fun showFullGif(gif: GifMvvm) {}
}