package com.free.movies.domain.models

import com.free.movies.utils.HLS
import com.free.movies.utils.MP4

data class Stream(val quality: Quality, val link: String)

fun mapStreams(data: List<com.free.movies.data.models.Stream>?): List<Stream> {
    return data?.map {
        Stream(quality = mapQuality(it.quality), link = mapLink(it.streams))
    } ?: emptyList()
}

fun mapLink(data: Map<String, String>): String {
    return data[HLS] ?: data[MP4] ?: ""
}

fun mapQuality(value: String): Quality {
    return when (value) {
        "360p" -> {
            Quality.LOW
        }

        "480p" -> {
            Quality.MEDIUM
        }

        "720p" -> {
            Quality.HD
        }

        "1080p" -> {
            Quality.FULL_HD
        }

        else -> Quality.LOW
    }
}
