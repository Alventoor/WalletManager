package com.github.alventoor.walletmanager.ui.component

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ButtonText(text: String) {
    Text(
        text = text.uppercase(),
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun TitleText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h5
    )
}