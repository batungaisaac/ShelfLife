package com.example.shelflife.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shelflife.data.FoodItem
import com.example.shelflife.data.ItemStatus
import kotlinx.coroutines.launch

// Tabs in the bottom bar.
private enum class Screen(val route: String, val title: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Filled.Home),
    ExpiringSoon("expiring_soon", "Expiring Soon", Icons.Filled.Warning)
}

// Screens opened on top of a tab (no bottom bar, back arrow instead).
private const val ADD_ITEM_ROUTE = "add_item"
private const val EDIT_ITEM_ROUTE = "edit_item/{itemId}"
private fun editItemRoute(id: Long) = "edit_item/$id"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfLifeApp(viewModel: FoodViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTab = Screen.entries.firstOrNull { it.route == currentRoute }
    val title = when (currentRoute) {
        ADD_ITEM_ROUTE -> "Add Item"
        EDIT_ITEM_ROUTE -> "Edit Item"
        else -> (currentTab ?: Screen.Home).title
    }

    // Snackbar lives at app level so it survives switching tabs.
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val onMark: (FoodItem, ItemStatus) -> Unit = { item, status ->
        viewModel.markItem(item, status)
        scope.launch {
            // Replace any snackbar still showing instead of queueing behind it.
            snackbarHostState.currentSnackbarData?.dismiss()
            val verb = if (status == ItemStatus.USED) "used" else "binned"
            val result = snackbarHostState.showSnackbar(
                message = "${item.name} marked as $verb",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) viewModel.undoMark(item)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (currentTab == null && currentRoute != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (currentTab == Screen.Home) {
                FloatingActionButton(onClick = { navController.navigate(ADD_ITEM_ROUTE) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add item")
                }
            }
        },
        bottomBar = {
            if (currentTab != null) {
                NavigationBar {
                    Screen.entries.forEach { screen ->
                        NavigationBarItem(
                            selected = screen == currentTab,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel,
                    onMark = onMark,
                    onItemClick = { navController.navigate(editItemRoute(it.id)) }
                )
            }
            composable(Screen.ExpiringSoon.route) { ExpiringSoonScreen(viewModel) }
            composable(ADD_ITEM_ROUTE) {
                AddItemScreen(viewModel, itemId = null, onDone = { navController.popBackStack() })
            }
            composable(
                EDIT_ITEM_ROUTE,
                arguments = listOf(navArgument("itemId") { type = NavType.LongType })
            ) { entry ->
                AddItemScreen(
                    viewModel,
                    itemId = entry.arguments?.getLong("itemId"),
                    onDone = { navController.popBackStack() }
                )
            }
        }
    }
}
