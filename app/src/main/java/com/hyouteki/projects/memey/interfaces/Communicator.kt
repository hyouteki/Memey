package com.hyouteki.projects.memey.interfaces

import com.hyouteki.projects.memey.models.GifMvvm
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.models.RandomMeme

interface Communicator {
    fun refreshFavMemeFragment() {}
    fun refreshFavGifFragment() {}
    fun deleteCurrentLocalMeme() {}
    fun shareMeme(meme: Meme) {}
    fun randomMemeShareClickMethod(meme: RandomMeme) {}
    fun shareButtonClickMethod(meme: RandomMeme) {}
    fun favoriteButtonClickMethod(meme: RandomMeme) {}
    fun redirectButtonClickMethod(meme: RandomMeme) {}
    fun localMemeFavoriteClickMethod(meme: Meme) {}
    fun profileClickMethod(meme: Meme) {}
    fun loadMyMemes() {}
    fun openGif(gif: GifMvvm) {}
    fun openMemeOptions(meme: Meme) {}
    fun showFullTitle(title: String) {}
}