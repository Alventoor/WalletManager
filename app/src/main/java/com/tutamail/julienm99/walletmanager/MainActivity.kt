package com.tutamail.julienm99.walletmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tutamail.julienm99.walletmanager.ui.home.homeRoute
import com.tutamail.julienm99.walletmanager.ui.home.homeScreen
import com.tutamail.julienm99.walletmanager.ui.settings.navigateToSettings
import com.tutamail.julienm99.walletmanager.ui.settings.settingsScreen
import com.tutamail.julienm99.walletmanager.ui.theme.AppTheme
import com.tutamail.julienm99.walletmanager.ui.transactioncategory.creation.navigateToTransactionCategoryCreation
import com.tutamail.julienm99.walletmanager.ui.transactioncategory.creation.transactionCategoryCreationDialog
import com.tutamail.julienm99.walletmanager.ui.transactioncategory.deletion.navigateToTransactionCategoryDeletion
import com.tutamail.julienm99.walletmanager.ui.transactioncategory.deletion.transactionCategoryDeletionDialog
import com.tutamail.julienm99.walletmanager.ui.transactioncreation.navigateToTransactionCreation
import com.tutamail.julienm99.walletmanager.ui.transactioncreation.transactionCreationScreen
import com.tutamail.julienm99.walletmanager.ui.wallet.wallet.navigateToWallet
import com.tutamail.julienm99.walletmanager.ui.wallet.wallet.popUpToWallet
import com.tutamail.julienm99.walletmanager.ui.wallet.wallet.walletScreen
import com.tutamail.julienm99.walletmanager.ui.wallet.creation.navigateToWalletCreation
import com.tutamail.julienm99.walletmanager.ui.wallet.creation.walletCreationRoute
import com.tutamail.julienm99.walletmanager.ui.wallet.creation.walletCreationScreen
import com.tutamail.julienm99.walletmanager.ui.wallet.notfound.navigateToWalletNotFound
import com.tutamail.julienm99.walletmanager.ui.wallet.notfound.walletNotFoundScreen
import com.tutamail.julienm99.walletmanager.ui.wallet.settings.navigateToWalletSettings
import com.tutamail.julienm99.walletmanager.ui.wallet.settings.walletSettingsScreen

private val transitionAnimation = tween<IntOffset>(200, easing = EaseIn)
private val popTransitionAnimation = tween<IntOffset>(200, easing = EaseOut)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = homeRoute,
                    enterTransition = {
                        slideIntoContainer(
                            animationSpec = transitionAnimation,
                            towards = AnimatedContentTransitionScope.SlideDirection.Start
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            animationSpec = transitionAnimation,
                            towards = AnimatedContentTransitionScope.SlideDirection.Start
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            animationSpec = popTransitionAnimation,
                            towards = AnimatedContentTransitionScope.SlideDirection.End
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            animationSpec = popTransitionAnimation,
                            towards = AnimatedContentTransitionScope.SlideDirection.End
                        )
                    },
                ) {
                    homeScreen(
                        onSettingsClicked = navController::navigateToSettings,
                        onWalletClicked = navController::navigateToWallet,
                        onAddWalletClicked = navController::navigateToWalletCreation,
                    )

                    settingsScreen(
                        onBackClicked = navController::popBackStack,
                        onDeleteTransactionCategoryClicked = navController::navigateToTransactionCategoryDeletion
                    )

                    walletCreationScreen(
                        onBackClicked = navController::popBackStack,
                        onNavigateToWallet = { id ->
                            navController.navigateToWallet(id) {
                                popUpTo(walletCreationRoute) {
                                    inclusive = true
                                }
                            }
                        }
                    )

                    walletScreen(
                        onBackClicked = navController::popBackStack,
                        onNavigateToWalletNotFound = navController::navigateToWalletNotFound,
                        onWalletSettingsClicked = navController::navigateToWalletSettings,
                        onAddTransactionClicked = navController::navigateToTransactionCreation
                    )

                    walletSettingsScreen(
                        onBackClicked = navController::popBackStack,
                        onNavigateToWallet = navController::navigateToWallet,
                        onNavigateToHome = { navController.popBackStack(homeRoute, false) },
                        onNavigateToWalletNotFound = navController::navigateToWalletNotFound
                    )

                    walletNotFoundScreen(
                        onBackClicked = { navController.popBackStack(homeRoute, false) },
                        /* Fix a bug when navigating during another navigation, where the old next screen
                         * transition is canceled, displaying a blank screen instead of the transition */
                        enterTransition = EnterTransition.None
                    )

                    transactionCreationScreen(
                        onBackClicked = navController::popBackStack,
                        onNavigateToWallet = { id ->
                            navController.navigateToWallet(id) {
                                popUpToWallet(true)
                            }
                        },
                        onNavigateToWalletNotFound = navController::navigateToWalletNotFound,
                        onAddTransactionCategoryClicked = navController::navigateToTransactionCategoryCreation
                    )

                    transactionCategoryCreationDialog(onBackClicked = navController::popBackStack)

                    transactionCategoryDeletionDialog(onDismiss = navController::popBackStack)
                }
            }
        }
    }
}