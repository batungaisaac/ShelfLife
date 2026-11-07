package com.example.shelflife.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shelflife.data.FoodItem
import com.example.shelflife.data.ItemStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Tabs in the bottom bar. label is the short name under the icon, title goes in the top bar.
private enum class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val label: String = title
) {
    Home("home", "Home", Icons.Filled.Home),
    ExpiringSoon("expiring_soon", "Expiring Soon", Icons.Filled.Warning, label = "Expiring"),
    ShoppingList("shopping_list", "Shopping List", Icons.Filled.ShoppingCart, label = "Shopping")
}

// Screens opened on top of a tab (no bottom bar, back arrow instead).
private const val ADD_ITEM_ROUTE = "add_item"
private const val EDIT_ITEM_ROUTE = "edit_item/{itemId}"
private fun editItemRoute(id: Long) = "edit_item/$id"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfLifeApp(
    viewModel: FoodViewModel,
    shoppingListViewModel: ShoppingListViewModel = viewModel()
) {
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
    val snackbarJob = remember { mutableStateOf<Job?>(null) }

    // Shows a snackbar whose Undo puts the item back exactly as it was. If the item was
    // marked Used and not undone, follows up by offering to add it to the Shopping List.
    fun showUndo(message: String, original: FoodItem, offerShoppingList: Boolean = false) {
        // Cancelling the previous job replaces its snackbar (and skips its offer)
        // instead of queueing behind it.
        snackbarJob.value?.cancel()
        snackbarJob.value = scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoMark(original)
                return@launch
            }
            if (!offerShoppingList || shoppingListViewModel.isOnList(original.name)) return@launch
            val add = snackbarHostState.showSnackbar(
                message = "Add ${original.name} to your shopping list?",
                actionLabel = "Add",
                duration = SnackbarDuration.Short
            )
            if (add == SnackbarResult.ActionPerformed) shoppingListViewModel.add(original.name)
        }
    }

    val onMark: (FoodItem, ItemStatus) -> Unit = { item, status ->
        viewModel.markItem(item, status)
        val verb = if (status == ItemStatus.USED) "used" else "binned"
        showUndo("${item.name} marked as $verb", item, offerShoppingList = status == ItemStatus.USED)
    }

    // Using the last of an item marks it Used.
    val onUseSome: (FoodItem, Double) -> Unit = { item, amount ->
        val left = item.quantityRemaining - amount
        if (left <= 0.0) {
            viewModel.markItem(item, ItemStatus.USED, quantityRemaining = 0.0)
            showUndo("${item.name} used up and marked as used", item, offerShoppingList = true)
        } else {
            viewModel.useSome(item, amount)
            showUndo("Used ${formatQuantity(amount)} of ${item.name}, ${formatQuantity(left)} left", item)
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
                            label = { Text(screen.label) }
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
            composable(Screen.ShoppingList.route) { ShoppingListScreen(shoppingListViewModel) }
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
                    onDone = { navController.popBackStack() },
                    onUseSome = onUseSome
                )
            }
        }
    }
}
