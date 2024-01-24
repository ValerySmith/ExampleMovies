package com.free.movies.domain.models

import androidx.annotation.StringRes
import com.free.movies.common.R

enum class Quality(@StringRes val value: Int, val pos: Int) {
    LOW(R.string.low, 1),
    MEDIUM(R.string.medium, 2),
    HD(R.string.high, 3),
    FULL_HD(R.string.best, 4);
    companion object {
        fun Companion.fromValue(value: Int): Quality {
            return when (value) {
                1 -> LOW
                2 -> MEDIUM
                3 -> HD
                4 -> FULL_HD
                else -> LOW
            }
        }
    }
}
