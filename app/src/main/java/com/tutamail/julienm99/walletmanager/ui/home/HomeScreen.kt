package com.tutamail.julienm99.walletmanager.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tutamail.julienm99.walletmanager.R
import com.tutamail.julienm99.walletmanager.core.model.Wallet
import com.tutamail.julienm99.walletmanager.ui.component.*
import com.tutamail.julienm99.walletmanager.ui.theme.AppTheme
import com.tutamail.julienm99.walletmanager.ui.util.formatCurrency
import com.tutamail.julienm99.walletmanager.ui.wallet.deletion.WalletDeletionDialog
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

@Composable
fun HomeScreen(
    onSettingsClicked: () -> Unit,
    onWalletClicked: (walletId: Long) -> Unit,
    onAddWalletClicked: () -> Unit,
    viewModel: HomeViewModel
) {
    val walletsUiState by viewModel.walletsUiState.collectAsStateWithLifecycle()

    HomeScreen(
        walletsUiState = walletsUiState,
        onSettingsClicked = onSettingsClicked,
        onWalletClicked = onWalletClicked,
        onAddWalletClicked = onAddWalletClicked,
        onDeleteModeSelected = viewModel::onWalletSelectionEnabled,
        onCancelClicked = viewModel::onClearSelectionList,
        onDeleteClicked = viewModel::onWalletsDeletionClicked,
        onWalletSelected = viewModel::onWalletSelected,
    )
}

@Composable
private fun HomeScreen(
    walletsUiState: WalletsUiState,
    onSettingsClicked: () -> Unit,
    onWalletClicked: (walletId: Long) -> Unit,
    onAddWalletClicked: () -> Unit,
    onDeleteModeSelected: () -> Unit,
    onCancelClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onWalletSelected: (WalletUiState) -> Unit
) {
    var displayDeleteDialog by rememberSaveable { mutableStateOf(false) }
    // Without that, the drawer never close when we navigate outside the fragment
    val closeDrawer = rememberSaveable { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState()
    val drawerCoroutine = rememberCoroutineScope()

    val onNavigationIconClicked: () -> Unit = {
        drawerCoroutine.launch {
            scaffoldState.drawerState.open()
        }
    }

    LaunchedEffect(closeDrawer) {
        if (closeDrawer.value) {
            drawerCoroutine.launch {
                scaffoldState.drawerState.snapTo(DrawerValue.Closed)
            }

            closeDrawer.value = false
        }
    }

    if (displayDeleteDialog && walletsUiState is WalletsUiState.Loaded)
        WalletDeletionDialog(
            onDeletion = {
                onDeleteClicked()
                displayDeleteDialog = false
            },
            onCancel = {
                onCancelClicked()
                displayDeleteDialog = false
            },
            wallets = walletsUiState.wallets.filter { it.selected }.map { it.data }
        )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            when (walletsUiState.displayCheckbox) {
                true -> DeleteTopAppBar(
                    onDelete = { if (walletsUiState.hasSelectedWallets) displayDeleteDialog = true },
                    onCancel = onCancelClicked,
                    deleteActionDescription = stringResource(R.string.home_screen_delete_selected_wallets_button_description),
                    cancelActionDescription = stringResource(R.string.home_screen_delete_cancel_wallet_deletion_button_description)
                )

                false -> AppTopAppBar(
                    title = stringResource(R.string.home_screen_title),
                    icon = Icons.Filled.Menu,
                    iconDescription = stringResource(R.string.home_screen_navigation_description),
                    onNavigationClick = onNavigationIconClicked,
                    actions = {
                        AppBarMenu(
                            menuDescription = stringResource(R.string.home_screen_menu_button_description),
                            items = listOf(
                                stringResource(R.string.home_screen_menu_item_delete_wallets) to onDeleteModeSelected
                            )
                        )
                    }
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !walletsUiState.displayCheckbox,
                enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight + fullHeight / 2 }),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight + fullHeight / 2 })
            ) {
                FloatingActionButton(onClick = onAddWalletClicked) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.home_screen_add_wallet_button_description)
                    )
                }
            }
        },
        drawerGesturesEnabled = true,
        drawerContent = {
            HomeDrawer(closeDrawer, onSettingsClicked)
        }
    ) { contentPadding ->
        when (walletsUiState) {
            is WalletsUiState.Loading -> {
                Column(modifier = Modifier.padding(contentPadding).padding(rootPadding)) {
                    HeaderItem(stringResource(R.string.home_screen_wallet_grid_title))

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AppCircularProgressIndicator()
                    }
                }
            }

            is WalletsUiState.Empty -> {
                Column(modifier = Modifier.padding(contentPadding).padding(rootPadding)) {
                    HeaderItem(stringResource(R.string.home_screen_wallet_grid_title))

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_empty_wallet_list),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            is WalletsUiState.Loaded -> {
                LazyVerticalGrid(
                    modifier = Modifier.padding(contentPadding),
                    columns = GridCells.Adaptive(minSize = 192.dp),
                    contentPadding = rootPadding,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = "header"
                    ) {
                        HeaderItem(stringResource(R.string.home_screen_wallet_grid_title))
                    }

                    items(
                        items = walletsUiState.wallets,
                        key = { it.data.id },
                        contentType = { it },
                    ) {
                        WalletItem(
                            wallet = it,
                            displayCheckbox = walletsUiState.displayCheckbox,
                            onWalletClicked = onWalletClicked,
                            onWalletSelected = onWalletSelected
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun HeaderItem(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TitleText(
            modifier = Modifier.padding(bottom = 16.dp),
            text = text
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WalletItem(
    wallet: WalletUiState,
    displayCheckbox: Boolean,
    onWalletClicked: (Long) -> Unit,
    onWalletSelected: (WalletUiState) -> Unit
) {
    Card(
        modifier = Modifier.combinedClickable(
            onLongClick = { onWalletSelected(wallet) },
            onClick = {
                when (displayCheckbox) {
                    true -> onWalletSelected(wallet)
                    false -> onWalletClicked(wallet.data.id)
                }
            }
        ),
        elevation = cardElevation
    ) {
        Column(
            modifier = cardContentModifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = wallet.data.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = formatCurrency(wallet.data.currency, wallet.data.balance),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (displayCheckbox) {
                Checkbox(
                    checked = wallet.selected,
                    onCheckedChange = { onWalletSelected(wallet) }
                )
            }
        }
    }
}

@Composable
private fun HomeDrawer(
    closeDrawer: MutableState<Boolean>,
    onSettingsClicked: () -> Unit
) {
    val headerColor: Color
    val onHeaderColor: Color

    when (MaterialTheme.colors.isLight) {
        true -> {
            headerColor = MaterialTheme.colors.primary
            onHeaderColor = MaterialTheme.colors.onPrimary
        }

        false -> {
            headerColor = MaterialTheme.colors.surface
            onHeaderColor = MaterialTheme.colors.onSurface
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(color = MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = headerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.home_screen_drawer_title),
                fontWeight = FontWeight.Bold,
                color = onHeaderColor
            )
        }

        DrawerItem(
            text = stringResource(R.string.home_screen_drawer_settings_menu_item),
            onClick = {
                closeDrawer.value = true
                onSettingsClicked()
            }
        )
    }
}

@Composable
private fun DrawerItem(text: String, onClick: () -> Unit) {
    Text(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth(),
        text = text,
        textAlign = TextAlign.Center
    )
}

@Composable
@Preview
private fun LoadingHomeScreenPreview() {
    HomeScreenPreview(WalletsUiState.Loading)
}

@Composable
@Preview
private fun EmptyHomeScreenPreview() {
    HomeScreenPreview(WalletsUiState.Empty)
}

@Composable
@Preview
private fun LoadedHomeScreenPreview() {
    val wallets = listOf(
        WalletUiState(
            data = Wallet(
                name = "Wallet 1",
                currency = Currency.getInstance(Locale.FRANCE),
                balance = BigDecimal("1200.30"),
                id = 1
            ),
            selected = false
        ),
        WalletUiState(
            data = Wallet(
                name = "Wallet 2",
                currency = Currency.getInstance(Locale.US),
                balance = BigDecimal("1136"),
                id = 2
            ),
            selected = false
        )
    )

    val walletsUiState = WalletsUiState.Loaded(
        wallets = wallets,
        displayCheckbox = false,
        hasSelectedWallets = false
    )

    HomeScreenPreview(walletsUiState)
}

@Composable
private fun HomeScreenPreview(walletsUiState: WalletsUiState) {
    AppTheme {
        HomeScreen(
            walletsUiState = walletsUiState,
            onSettingsClicked = {},
            onWalletClicked = {},
            onAddWalletClicked = {},
            onDeleteModeSelected = {},
            onCancelClicked = {},
            onDeleteClicked = {},
            onWalletSelected = {}
        )
    }
}