package com.free.movies.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.free.movies.presentation.theme.AppThemeMobile

@Composable
fun SmallLoader(isLoading: Boolean = false) {
    if (!isLoading) return
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val progressIndicator = createRef()
        CircularProgressIndicator(
            modifier = Modifier
                .size(32.dp)
                .constrainAs(progressIndicator) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SmallLoaderPreview() {
    AppThemeMobile {
        SmallLoader(true)
    }
}