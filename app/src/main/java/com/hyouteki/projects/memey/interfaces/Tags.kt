package com.hyouteki.projects.memey.interfaces

interface Tags {
    companion object {
        // Default tabs
        const val MEME_TAB = "Memes"
        const val GIF_TAB = "Gifs"
        const val MEMEY_MEME_TAB = "MEMEY memes"

        // Meme tab layout
        const val MULTIPLE_MEME_LAYOUT = "Multiple meme layout"
        const val SINGLE_MEME_LAYOUT = "Single meme layout"

        // What to share
        const val IMAGE_LINK = "Image link"
        const val POST_LINK = "Post link"

        // Swipe gestures
        const val SHARE_MEME = "Share meme"
        const val FAVORITE_MEME = "Mark meme as favorite"
        const val REDIRECT_MEME = "Redirect meme"
    }
}