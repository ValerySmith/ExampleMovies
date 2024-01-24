package com.free.movies.ui.activities.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.free.movies.navigation.NavigationItem
import com.free.movies.presentation.theme.AppThemeMobile
import com.free.movies.ui.detailmovie.DetailMovieScreen
import com.free.movies.ui.movies.MoviesScreen
import com.free.movies.ui.player.PlayerScreen
import com.free.movies.ui.search.SearchScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppThemeMobile {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavHost(navController = rememberNavController())
                }
            }
            val viewModel = hiltViewModel<MainActivityViewModel>()
        }
    }

    @Composable
    fun AppNavHost(
        modifier: Modifier = Modifier,
        navController: NavHostController,
        startDestination: String = NavigationItem.Movies.route
    ) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            composable(NavigationItem.Movies.route) {
                MoviesScreen(navController = navController)
            }

            composable(route = NavigationItem.Search.route) {
                SearchScreen(navController = navController)
            }

            composable(
                route = NavigationItem.DetailMovie.routeWithArg,
                arguments = NavigationItem.DetailMovie.arguments
            ) {
                DetailMovieScreen(
                    navController = navController,
                    movieId = it.arguments?.getInt(NavigationItem.MOVIE_ID) ?: 0
                )
            }

            composable(
                route = NavigationItem.Player.routeWithArgs,
                arguments = NavigationItem.Player.arguments
            ) {
                PlayerScreen(
                    navController = navController,
                    mediaUrl = it.arguments?.getString(NavigationItem.MEDIA_URL) ?: "",
                    movieId = it.arguments?.getInt(NavigationItem.MOVIE_ID) ?: 0
                )
            }

        }
    }
}