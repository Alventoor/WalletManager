package com.github.alventoor.walletmanager.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val verticalRootPadding = 24.dp
val horizontalRootPadding = 16.dp
val rootPadding = PaddingValues(horizontal = horizontalRootPadding, vertical = verticalRootPadding)

val cardContentPadding = 28.dp
val cardContentModifier: Modifier = Modifier.padding(cardContentPadding)
val cardElevation = 2.5.dp;