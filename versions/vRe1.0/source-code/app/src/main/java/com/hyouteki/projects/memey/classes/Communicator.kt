package com.hyouteki.projects.memey.classes

import com.hyouteki.projects.memey.models.RandomMeme

interface Communicator {
    fun shareButtonClickMethod(meme: RandomMeme)
    fun favoriteButtonClickMethod(meme: RandomMeme)
    fun redirectButtonClickMethod(meme: RandomMeme)
}