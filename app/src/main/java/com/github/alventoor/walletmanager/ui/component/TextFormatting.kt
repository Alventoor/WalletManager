package com.github.alventoor.walletmanager.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.alventoor.walletmanager.R

@Composable
fun dateFormattedShort(date: Long?): String {
    return when (date) {
        null -> ""
        else -> stringResource(R.string.transaction_date_short, date, date, date)
    }
}