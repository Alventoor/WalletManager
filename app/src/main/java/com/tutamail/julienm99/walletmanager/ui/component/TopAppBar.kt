package com.tutamail.julienm99.walletmanager.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.tutamail.julienm99.walletmanager.R

@Composable
fun AppTopAppBar(
    title: String,
    icon: ImageVector,
    iconDescription: String,
    onNavigationClick: () -> Unit,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    TopAppBar(
        title = { Text(title) },
        actions = actions,
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconDescription
                )
            }
        },
    )
}

@Composable
fun DeleteTopAppBar(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    deleteActionDescription: String,
    cancelActionDescription: String,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onCancel) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = cancelActionDescription)
            }
        },
        actions = {
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = deleteActionDescription)
            }
        }
    )
}

@Composable
fun AppBarMenu(menuDescription: String, items: List<Pair<String, () -> Unit>>) {
    var menuExpanded: Boolean by rememberSaveable { mutableStateOf(false) }

    Box {
        IconButton(onClick = { menuExpanded = !menuExpanded }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = menuDescription
            )
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            for ((text, action) in items) {
                DropdownMenuItem(onClick = {
                    action()
                    menuExpanded = false
                }) {
                    Text(text)
                }
            }
        }
    }
}