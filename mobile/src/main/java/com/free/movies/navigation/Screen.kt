package com.free.movies.navigation

import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

enum class Screen(val routeName: String) {
    MOVIES("movies"),
    DETAIL_MOVIE("detailMovie"),
    PLAYER("player"),
    SEARCH("search")
}

sealed class NavigationItem(val route: String) {

    companion object {
        const val MOVIE_ID = "movieId"
        const val MEDIA_URL = "mediaUrl"
    }

    data object Movies : NavigationItem(Screen.MOVIES.routeName)

    data object DetailMovie : NavigationItem(Screen.DETAIL_MOVIE.routeName) {
        /**  withMultiParams
        val routeWithArg: String = "$route/{$R_MOVIE_ID}/{$R_AUDIO_ID}"
        val arguments = listOf(
        navArgument(R_MOVIE_ID) { type = NavType.IntType },
        navArgument(R_AUDIO_ID) { type = NavType.IntType }
        )**/
        val routeWithArg = "$route/{$MOVIE_ID}"
        val arguments = listOf(navArgument(MOVIE_ID) { type = NavType.IntType })
        fun NavController.navigateToDetailMovie(movieId: Int) = navigate("$route/$movieId")
    }

    data object Player : NavigationItem(Screen.PLAYER.routeName) {
        val routeWithArgs = "${route}/{$MEDIA_URL}/{$MOVIE_ID}"
        val arguments = listOf(
            navArgument(MEDIA_URL) { type = NavType.StringType },
            navArgument(MOVIE_ID) { type = NavType.IntType })

        fun NavController.navigateToPlayer(mediaUrl: String, movieId: Int) {
            val encodedUrl = URLEncoder.encode(mediaUrl, StandardCharsets.UTF_8.toString())
            navigate("$route/${encodedUrl}/$movieId")
        }
    }

    data object Search : NavigationItem(Screen.SEARCH.routeName) {
        fun NavController.navigateToSearch() = navigate(route)
    }
}
