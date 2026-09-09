package com.example.novexa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.novexa.data.local.entity.OrderEntity
import com.example.novexa.ui.components.NovexaHeader
import com.example.novexa.ui.components.SummaryRow
import com.example.ui.theme.*
import com.example.novexa.ui.viewmodel.NovexaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AccountScreen(
    viewModel: NovexaViewModel,
    onNavigateToOrder: (OrderEntity) -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onOpenInfoPage: (String, String) -> Unit,
    onAdminClick: () -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val orders by viewModel.userOrders.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Orders, 1: Wishlist, 2: Notifications, 3: Settings
    var showAuthDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NovexaHeader(
                cartItemCount = cartItems.sumOf { it.cartItem.quantity },
                wishlistCount = wishlist.size,
                onSearchClick = onSearchClick,
                onCartClick = onCartClick,
                onWishlistClick = onWishlistClick,
                onAdminClick = onAdminClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(NovexaBlueContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (currentUser?.name?.take(1) ?: "U").uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp,
                                    color = NovexaBlue
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentUser?.name ?: "Guest User",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NovexaBlack
                                )
                                Text(
                                    text = currentUser?.phone ?: "+8801712345678",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NovexaTextSecondary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (currentUser?.role == "SUPER_ADMIN") NovexaAccentRose else NovexaAccentEmerald)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = currentUser?.role ?: "CUSTOMER",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            IconButton(onClick = { showAuthDialog = true }) {
                                Icon(Icons.Default.SwitchAccount, contentDescription = "Switch or Login", tint = NovexaBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Quick Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NovexaBgLight)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ProfileStatItem(label = "Orders", value = "${orders.size}")
                            ProfileStatItem(label = "Wishlist", value = "${wishlist.size}")
                            ProfileStatItem(label = "Notifications", value = "${notifications.size}")
                        }
                    }
                }
            }

            // Tabs Selector
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = NovexaBlue,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Orders (${orders.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Wishlist", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Alerts", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Help & Policy", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Orders list
                    if (orders.isEmpty()) {
                        item {
                            EmptySectionCard(
                                icon = Icons.Outlined.ShoppingBag,
                                title = "No Orders Yet",
                                message = "Items you purchase will appear here with live tracking status."
                            )
                        }
                    } else {
                        items(orders) { order ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToOrder(order) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "#${order.orderNumber}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = NovexaBlack
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    when (order.orderStatus) {
                                                        "Delivered" -> Color(0xFFDCFCE7)
                                                        "Cancelled" -> Color(0xFFFFE4E6)
                                                        else -> NovexaBlueContainer
                                                    }
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = order.orderStatus,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = when (order.orderStatus) {
                                                    "Delivered" -> NovexaAccentEmerald
                                                    "Cancelled" -> NovexaAccentRose
                                                    else -> NovexaBlue
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Total: ৳${order.total.toInt()} • Method: ${order.paymentMethod}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NovexaTextSecondary
                                    )
                                    Text(
                                        text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(order.createdAt)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NovexaTextMuted
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = "Track Order →",
                                            fontWeight = FontWeight.Bold,
                                            color = NovexaBlue,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Wishlist
                    if (wishlist.isEmpty()) {
                        item {
                            EmptySectionCard(
                                icon = Icons.Outlined.FavoriteBorder,
                                title = "Your Wishlist is Empty",
                                message = "Tap the heart icon on any product to save it for later."
                            )
                        }
                    } else {
                        items(wishlist) { product ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToProduct(product.id) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Text("৳${product.price.toInt()}", fontWeight = FontWeight.Bold, color = NovexaBlue)
                                    }
                                    Button(
                                        onClick = { viewModel.addToCart(product.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NovexaBlack),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Add to Cart", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Notifications
                    if (notifications.isEmpty()) {
                        item {
                            EmptySectionCard(
                                icon = Icons.Outlined.Notifications,
                                title = "No Notifications",
                                message = "You're all caught up with orders and discounts."
                            )
                        }
                    } else {
                        items(notifications) { notif ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(notif.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            text = SimpleDateFormat("dd MMM", Locale.US).format(Date(notif.createdAt)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NovexaTextMuted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(notif.message, style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Static Policies & Help
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Customer Support & Legal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(10.dp))

                                PolicyItem("About Novexa Bangladesh") { onOpenInfoPage("About Us", "ABOUT") }
                                PolicyItem("Contact Customer Service") { onOpenInfoPage("Contact Us", "CONTACT") }
                                PolicyItem("Frequently Asked Questions (FAQ)") { onOpenInfoPage("FAQ", "FAQ") }
                                PolicyItem("Return, Replacement & Refund Policy") { onOpenInfoPage("Return & Refund", "RETURN") }
                                PolicyItem("Privacy Policy & Data Security") { onOpenInfoPage("Privacy Policy", "PRIVACY") }
                                PolicyItem("Terms of Service") { onOpenInfoPage("Terms & Conditions", "TERMS") }
                            }
                        }
                    }
                }
            }
        }
    }

    // Switch account / auth dialog
    if (showAuthDialog) {
        AlertDialog(
            onDismissRequest = { showAuthDialog = false },
            title = { Text("Account Role Switcher", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select a profile to experience the full customer or administrator flow:")

                    Button(
                        onClick = {
                            viewModel.login("customer@novexa.com.bd", "user123")
                            showAuthDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Rafiqul Islam (Customer)")
                    }

                    Button(
                        onClick = {
                            viewModel.login("admin@novexa.com.bd", "admin123")
                            showAuthDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NovexaBlack),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Novexa Admin (Super Admin)")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAuthDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun ProfileStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = NovexaBlack)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = NovexaTextSecondary)
    }
}

@Composable
fun EmptySectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NovexaTextMuted, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
        }
    }
}

@Composable
fun PolicyItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = NovexaTextPrimary)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NovexaTextMuted, modifier = Modifier.size(18.dp))
    }
}
