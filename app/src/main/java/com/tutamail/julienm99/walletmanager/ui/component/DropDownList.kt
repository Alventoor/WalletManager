package com.tutamail.julienm99.walletmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> AppDropDownList(
    items: List<T>,
    selectedItem: T,
    itemDescription: (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
) {
    Box(modifier = modifier) {
        var expanded: Boolean by remember { mutableStateOf(false) }

        Column {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = itemDescription(selectedItem),
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                label = if (label == null) null else { { Text(label) } },
                trailingIcon = {
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                items.forEach { item ->
                    DropdownMenuItem(onClick = {
                        onItemSelected(item)
                        expanded = false
                    }) {
                        Text(itemDescription(item))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.matchParentSize()
            .background(Color.Transparent)
            .padding(10.dp)
            .clickable(enabled = enabled) { expanded = true }
        )
    }
}