package com.tutamail.julienm99.walletmanager.ui.component

import androidx.compose.animation.core.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun AppCircularProgressIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val progressAnimationValue by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 360.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, delayMillis = 0, easing = LinearEasing)
        )
    )

    CircularProgressIndicator(modifier = Modifier.rotate(progressAnimationValue))
}