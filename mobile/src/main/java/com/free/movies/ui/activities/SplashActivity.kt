package com.free.movies.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.free.movies.R
import com.free.movies.presentation.theme.AppThemeMobile
import com.free.movies.presentation.theme.Purple40
import com.free.movies.presentation.theme.Purple80
import com.free.movies.ui.activities.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()

        }
    }

    @Composable
    fun SplashScreen() {
        LaunchedEffect(key1 = true) {
            delay(2000)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Purple40), contentAlignment = Alignment.Center
        ) {

            Image(
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.FillBounds,
                painter = painterResource(R.drawable.ic_play_circle),
                contentDescription = null
            )
        }

    }

    @Preview(showBackground = true)
    @Composable
    fun SplashScreenPreview() {
        AppThemeMobile {
            SplashScreen()
        }
    }
}