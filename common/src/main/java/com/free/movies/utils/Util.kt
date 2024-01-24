package com.free.movies.utils

import android.util.Log
import android.util.Log.e
import com.free.movies.domain.models.Quality
import com.free.movies.domain.models.Stream

fun decodeUnicodeEscapeSequences(input: String): String {
    val pattern = Regex("\\\\u([0-9a-fA-F]{4})")
    return pattern.replace(input) { matchResult ->
        val unicodeValue = matchResult.groupValues[1]
        val decimalValue = unicodeValue.toInt(16)
        decimalValue.toChar().toString()
    }
}

fun logs(msg: String) =
    e("TEST", msg)

fun findLinkByQuality(streams: List<Stream>, defaultQuality: Quality): String {
    var link = ""
    streams.forEachIndexed { index, stream ->
        if (stream.quality == defaultQuality) {
            link = stream.link
            return@forEachIndexed
        } else if (index > 0) {
            link = streams[index - 1].link
        }
    }
    return link
}