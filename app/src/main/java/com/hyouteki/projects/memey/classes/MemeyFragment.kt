package com.hyouteki.projects.memey.classes

import androidx.fragment.app.Fragment

open class MemeyFragment(
    private val title: String,
    private val actionId: Int
) : Fragment() {
    companion object {
        const val NO_ACTION = 0
        const val REFRESH_ACTION = 1
        const val SEARCH_ACTION = 2
        const val RANKING_ACTION = 3
        const val ADD_ACTION = 4
        const val REFRESH_SEARCH_ACTION = 5
    }

    fun getTitle() = this.title
    fun getAction() = this.actionId
}