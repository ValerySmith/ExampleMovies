package com.free.movies.presentation.components

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.free.movies.common.R
import com.free.movies.presentation.viewmodels.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.FROYO)
@Composable
fun PlayerControls(
    state: PlayerViewModel.State,
    modifier: Modifier = Modifier,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit,
    onFullScreen: () -> Unit = {},
    onSetQuality: () -> Unit,
    onSetAudio: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onBack: () -> Unit
) {

    val sliderPosition = remember {
        mutableFloatStateOf(state.currentPosition)
    }

    val context = LocalContext.current

    val isTv =
        context.resources.configuration.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION

    TopAppBar(modifier = Modifier, colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent.copy(alpha = 0.5f),
        navigationIconContentColor = Color.White,
        titleContentColor = Color.White
    ),
        title = { Text(text = state.title) },
        navigationIcon = {
            IconButton(onClick = {
                onBack.invoke()
            }) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
            }
        })

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent.copy(alpha = 0.3f))
    ) {
        val (pause, replay, forward) = createRefs()
        val (audio, quality) = createRefs()
        val (time, slider) = createRefs()
        createHorizontalChain(replay, pause, forward, chainStyle = ChainStyle.Packed)
        createHorizontalChain(audio, quality, chainStyle = ChainStyle.Packed)
        IconButton(modifier = Modifier
            .size(64.dp)
            .constrainAs(pause) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }, onClick = { onPauseToggle.invoke() }) {
            Icon(
                painterResource(id = if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                modifier = Modifier.size(64.dp),
                contentDescription = null
            )
        }
        IconButton(modifier = Modifier
            .padding(start = 32.dp)
            .constrainAs(forward) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }, onClick = { onForwardClick.invoke() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_forward_5),
                modifier = Modifier.size(64.dp),
                contentDescription = null
            )
        }

        IconButton(modifier = Modifier
            .padding(end = 32.dp)
            .constrainAs(replay) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }, onClick = { onReplayClick.invoke() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_replay_5),
                modifier = Modifier.size(64.dp),
                contentDescription = null
            )
        }

        Text(text = "${state.passedTime}/${state.leftTime}", modifier = Modifier.constrainAs(time) {
            bottom.linkTo(slider.top, margin = 8.dp)
            start.linkTo(slider.start, margin = 24.dp)
        })

        Slider(
            value = sliderPosition.floatValue,
            onValueChange = {
                sliderPosition.floatValue = it
                onSeekTo.invoke(it.toLong())
            },
            valueRange = 0f..state.duration,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 24.dp, end = 24.dp)
                .constrainAs(slider) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(quality.top)
                })

        IconButton(modifier = Modifier
            .padding(end = 16.dp, start = 16.dp, bottom = 16.dp)
            .size(32.dp)
            .constrainAs(quality) {
                bottom.linkTo(parent.bottom)
            }, onClick = { onSetQuality.invoke() }) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_quality),
                    modifier = Modifier
                        .size(32.dp),
                    contentDescription = null
                )
            }
        }

        IconButton(modifier = Modifier
            .padding(end = 16.dp, start = 16.dp, bottom = 16.dp)
            .size(32.dp)
            .constrainAs(audio) {
                bottom.linkTo(parent.bottom)
            }, onClick = { onSetAudio.invoke() }) {

            Icon(
                painter = painterResource(id = R.drawable.ic_language),
                modifier = Modifier
                    .size(32.dp),
                contentDescription = null
            )
        }
    }

}