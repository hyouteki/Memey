package com.hyouteki.projects.memey.models

data class RandomMeme(
    val photoUrl: String = "",
    val postUrl: String = "",
    val title: String = "",
    val nsfw: Boolean = false,
    val upvoteCount: String = "0",
    var liked: Boolean = false
)