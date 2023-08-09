package com.hyouteki.projects.memey.models

data class Meme(
    val uid: String = "",
    val mid: String = "",
    val photoUrl: String = "",
    val postUrl: String = "",
    val title: String = "",
    val nsfw: Boolean = false,
    val upvoteCount: String = "0"
)
