package com.example.novexa.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.novexa.data.local.NovexaDatabase
import com.example.novexa.data.local.entity.OrderEntity
import com.example.novexa.data.repository.NovexaRepository
import com.example.novexa.ui.screens.*
import com.example.ui.theme.*
import com.example.novexa.ui.viewmodel.AdminViewModel
import com.example.novexa.ui.viewmodel.NovexaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NovexaDestinations {
    const val HOME = "home"
    const val PRODUCTS = "products"
    const val PRODUCT_DETAILS = "product_details"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDER_TRACKING = "order_tracking"
    const val ACCOUNT = "account"
    const val ADMIN = "admin"
    const val INFO = "info"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovexaApp() {
    val context = LocalContext.current
    val appScope = remember { CoroutineScope(Dispatchers.IO) }
    val database = remember { NovexaDatabase.getDatabase(context, appScope) }
    val repository = remember { NovexaRepository(database) }

    val novexaViewModel: NovexaViewModel = viewModel(
        factory = NovexaViewModel.Factory(repository)
    )
    val adminViewModel: AdminViewModel = viewModel(
        factory = AdminViewModel.Factory(repository)
    )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val cartItems by novexaViewModel.cartItems.collectAsState()
    val userMessage by novexaViewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var trackingOrder by remember { mutableStateOf<OrderEntity?>(null) }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            novexaViewModel.clearUserMessage()
        }
    }

    val showBottomBar = currentRoute in listOf(
        NovexaDestinations.HOME,
        NovexaDestinations.PRODUCTS,
        NovexaDestinations.CART,
        NovexaDestinations.ACCOUNT
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val cartBadgeCount = cartItems.sumOf { it.cartItem.quantity }

                    NavigationBarItem(
                        selected = currentRoute == NovexaDestinations.HOME,
                        onClick = {
                            navController.navigate(NovexaDestinations.HOME) {
                                popUpTo(NovexaDestinations.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (currentRoute == NovexaDestinations.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NovexaBlue,
                            selectedTextColor = NovexaBlue,
                            indicatorColor = NovexaBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentRoute == NovexaDestinations.PRODUCTS,
                        onClick = {
                            navController.navigate(NovexaDestinations.PRODUCTS) {
                                popUpTo(NovexaDestinations.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (currentRoute == NovexaDestinations.PRODUCTS) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                                contentDescription = "Shop"
                            )
                        },
                        label = { Text("Shop", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NovexaBlue,
                            selectedTextColor = NovexaBlue,
                            indicatorColor = NovexaBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_products")
                    )

                    NavigationBarItem(
                        selected = currentRoute == NovexaDestinations.CART,
                        onClick = {
                            navController.navigate(NovexaDestinations.CART) {
                                popUpTo(NovexaDestinations.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (cartBadgeCount > 0) {
                                        Badge(containerColor = NovexaBlue, contentColor = Color.White) {
                                            Text("$cartBadgeCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    if (currentRoute == NovexaDestinations.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                    contentDescription = "Cart"
                                )
                            }
                        },
                        label = { Text("Cart", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NovexaBlue,
                            selectedTextColor = NovexaBlue,
                            indicatorColor = NovexaBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_cart")
                    )

                    NavigationBarItem(
                        selected = currentRoute == NovexaDestinations.ACCOUNT,
                        onClick = {
                            navController.navigate(NovexaDestinations.ACCOUNT) {
                                popUpTo(NovexaDestinations.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (currentRoute == NovexaDestinations.ACCOUNT) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Account"
                            )
                        },
                        label = { Text("Account", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NovexaBlue,
                            selectedTextColor = NovexaBlue,
                            indicatorColor = NovexaBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_account")
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NovexaDestinations.HOME,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Home Screen
            composable(NovexaDestinations.HOME) {
                HomeScreen(
                    viewModel = novexaViewModel,
                    onProductClick = { productId ->
                        navController.navigate("${NovexaDestinations.PRODUCT_DETAILS}/$productId")
                    },
                    onCategoryClick = { catId ->
                        novexaViewModel.selectCategory(catId)
                        navController.navigate(NovexaDestinations.PRODUCTS)
                    },
                    onViewAllProducts = {
                        novexaViewModel.resetFilters()
                        navController.navigate(NovexaDestinations.PRODUCTS)
                    },
                    onOpenInfoPage = { title, type ->
                        navController.navigate("${NovexaDestinations.INFO}/$title/$type")
                    },
                    onCartClick = { navController.navigate(NovexaDestinations.CART) },
                    onWishlistClick = { navController.navigate(NovexaDestinations.ACCOUNT) },
                    onAdminClick = { navController.navigate(NovexaDestinations.ADMIN) },
                    onSearchClick = { navController.navigate(NovexaDestinations.PRODUCTS) }
                )
            }

            // Products Catalog / Search Screen
            composable(NovexaDestinations.PRODUCTS) {
                ProductsSearchScreen(
                    viewModel = novexaViewModel,
                    onProductClick = { productId ->
                        navController.navigate("${NovexaDestinations.PRODUCT_DETAILS}/$productId")
                    },
                    onBack = { navController.popBackStack() },
                    onCartClick = { navController.navigate(NovexaDestinations.CART) },
                    onWishlistClick = { navController.navigate(NovexaDestinations.ACCOUNT) },
                    onAdminClick = { navController.navigate(NovexaDestinations.ADMIN) }
                )
            }

            // Product Details Screen
            composable(
                route = "${NovexaDestinations.PRODUCT_DETAILS}/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 1L
                ProductDetailsScreen(
                    productId = productId,
                    viewModel = novexaViewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToCart = { navController.navigate(NovexaDestinations.CART) },
                    onNavigateToCheckout = { navController.navigate(NovexaDestinations.CHECKOUT) }
                )
            }

            // Cart Screen
            composable(NovexaDestinations.CART) {
                CartScreen(
                    viewModel = novexaViewModel,
                    onBack = { navController.popBackStack() },
                    onProceedToCheckout = { navController.navigate(NovexaDestinations.CHECKOUT) },
                    onContinueShopping = { navController.navigate(NovexaDestinations.PRODUCTS) }
                )
            }

            // Checkout Screen
            composable(NovexaDestinations.CHECKOUT) {
                CheckoutScreen(
                    viewModel = novexaViewModel,
                    onBack = { navController.popBackStack() },
                    onOrderPlaced = { order ->
                        trackingOrder = order
                        navController.navigate(NovexaDestinations.ORDER_TRACKING) {
                            popUpTo(NovexaDestinations.HOME)
                        }
                    }
                )
            }

            // Order Tracking Screen
            composable(NovexaDestinations.ORDER_TRACKING) {
                val latestOrder by novexaViewModel.latestOrder.collectAsState()
                val orderToShow = trackingOrder ?: latestOrder
                if (orderToShow != null) {
                    OrderTrackingScreen(
                        order = orderToShow,
                        viewModel = novexaViewModel,
                        onBack = { navController.popBackStack() },
                        onContinueShopping = {
                            navController.navigate(NovexaDestinations.HOME) {
                                popUpTo(NovexaDestinations.HOME) { inclusive = true }
                            }
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No active order to track")
                    }
                }
            }

            // Account Screen
            composable(NovexaDestinations.ACCOUNT) {
                AccountScreen(
                    viewModel = novexaViewModel,
                    onNavigateToOrder = { order ->
                        trackingOrder = order
                        navController.navigate(NovexaDestinations.ORDER_TRACKING)
                    },
                    onNavigateToProduct = { pId ->
                        navController.navigate("${NovexaDestinations.PRODUCT_DETAILS}/$pId")
                    },
                    onOpenInfoPage = { title, type ->
                        navController.navigate("${NovexaDestinations.INFO}/$title/$type")
                    },
                    onAdminClick = { navController.navigate(NovexaDestinations.ADMIN) },
                    onCartClick = { navController.navigate(NovexaDestinations.CART) },
                    onWishlistClick = { /* Already on account */ },
                    onSearchClick = { navController.navigate(NovexaDestinations.PRODUCTS) }
                )
            }

            // Admin Dashboard Screen
            composable(NovexaDestinations.ADMIN) {
                AdminDashboardScreen(
                    adminViewModel = adminViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Static / Information Screen
            composable(
                route = "${NovexaDestinations.INFO}/{title}/{type}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("type") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: "Information"
                val type = backStackEntry.arguments?.getString("type") ?: "ABOUT"
                InfoPageScreen(
                    title = title,
                    type = type,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
