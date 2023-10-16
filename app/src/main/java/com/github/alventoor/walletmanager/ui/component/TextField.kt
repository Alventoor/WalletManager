package com.github.alventoor.walletmanager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onClearButtonClick: () -> Unit,
    clearButtonContentDescription: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    RawAppTextField(
        modifier = modifier,
        value = value,
        label = label,
        onValueChange = onValueChange,
        onClearButtonClick = onClearButtonClick,
        clearButtonContentDescription = clearButtonContentDescription,
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun ClickableAppTextField(
    value: String,
    label: String,
    onClick: () -> Unit,
    onClearButtonClick: () -> Unit,
    clearButtonContentDescription: String,
    modifier: Modifier = Modifier,
) {
    RawAppTextField(
        modifier = modifier,
        value = value,
        onValueChange = {},
        label = label,
        onClearButtonClick = onClearButtonClick,
        clearButtonContentDescription = clearButtonContentDescription,
        onClick = onClick
    )
}

@Composable
private fun RawAppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onClearButtonClick: () -> Unit,
    clearButtonContentDescription: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onClick: (() -> Unit)? = null,
) {
    val finalModifier: Modifier
    val enabled: Boolean
    val colors: TextFieldColors

    if (onClick != null) {
        enabled = false
        finalModifier = modifier.clickable { onClick() }
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = MaterialTheme.colors.onSurface,
            disabledBorderColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.disabled),
            disabledLeadingIconColor = MaterialTheme.colors.onSurface,
            trailingIconColor = MaterialTheme.colors.primary,
            disabledTrailingIconColor = MaterialTheme.colors.primary,
            disabledLabelColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium),
            disabledPlaceholderColor = MaterialTheme.colors.onSurface
        )
    } else {
        enabled = true
        finalModifier = modifier
        colors = TextFieldDefaults.outlinedTextFieldColors(
            trailingIconColor = MaterialTheme.colors.primary
        )
    }

    OutlinedTextField(
        modifier = finalModifier,
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        colors = colors,
        trailingIcon = {
            IconButton(onClick = onClearButtonClick) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = clearButtonContentDescription,
                )
            }
        }
    )
}