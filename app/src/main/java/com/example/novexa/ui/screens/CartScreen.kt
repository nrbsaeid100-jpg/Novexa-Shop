package com.example.novexa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.novexa.data.repository.CartItemWithProduct
import com.example.novexa.ui.components.SummaryRow
import com.example.ui.theme.*
import com.example.novexa.ui.viewmodel.NovexaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: NovexaViewModel,
    onBack: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val pricing by viewModel.cartPricing.collectAsState()
    val appliedCoupon by viewModel.appliedCouponCode.collectAsState()
    val isInsideCity by viewModel.isInsideCity.collectAsState()

    var couponInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_cart_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NovexaBlack)
                    }
                    Text(
                        text = "Shopping Cart (${cartItems.sumOf { it.cartItem.quantity }})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NovexaBlack
                    )
                }
            }
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Grand Total",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NovexaTextSecondary
                                )
                                Text(
                                    text = "৳${pricing.total.toInt()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = NovexaBlue
                                )
                            }

                            Button(
                                onClick = onProceedToCheckout,
                                enabled = pricing.outOfStockItems.isEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("btn_proceed_checkout")
                            ) {
                                Text("Checkout", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        tint = NovexaTextMuted,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Cart is Empty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NovexaBlack
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Discover top deals and trending items in Bangladesh.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NovexaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onContinueShopping,
                        colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Start Shopping")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Out of stock warning banner
                if (pricing.outOfStockItems.isNotEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E6)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = NovexaAccentRose)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Some items exceed stock: ${pricing.outOfStockItems.joinToString(", ")}. Please adjust quantity before checkout.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NovexaAccentRose,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Cart Item Cards
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onQuantityChange = { newQty ->
                            viewModel.updateCartQuantity(item.cartItem.id, newQty, item.product.stockQuantity)
                        },
                        onRemove = { viewModel.removeFromCart(item.cartItem.id) }
                    )
                }

                // Shipping Location Selection
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Delivery Location (Bangladesh)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NovexaBlack
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FilterChip(
                                    selected = isInsideCity,
                                    onClick = { viewModel.setShippingLocation(true) },
                                    label = { Text("Inside Dhaka (৳60)") },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NovexaBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )

                                FilterChip(
                                    selected = !isInsideCity,
                                    onClick = { viewModel.setShippingLocation(false) },
                                    label = { Text("Outside Dhaka (৳120)") },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NovexaBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Coupon / Promo Code Input
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Have a Coupon?",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NovexaBlack
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (appliedCoupon != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NovexaBlueContainer)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NovexaAccentEmerald, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(appliedCoupon!!, fontWeight = FontWeight.Bold, color = NovexaBlue)
                                    }
                                    IconButton(
                                        onClick = { viewModel.removeCoupon() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove Coupon", tint = NovexaTextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = couponInput,
                                        onValueChange = { couponInput = it },
                                        placeholder = { Text("Enter code (e.g. NOVEXA100)") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_coupon_code")
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (couponInput.isNotBlank()) {
                                                viewModel.applyCoupon(couponInput)
                                                couponInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NovexaBlack),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("btn_apply_coupon")
                                    ) {
                                        Text("Apply")
                                    }
                                }
                            }

                            if (pricing.couponMessage != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = pricing.couponMessage!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (pricing.isCouponApplied) NovexaAccentEmerald else NovexaAccentRose,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Order Summary Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Price Details",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NovexaBlack
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            SummaryRow("Subtotal", "৳${pricing.subtotal.toInt()}")
                            if (pricing.discount > 0) {
                                SummaryRow("Discount", "-৳${pricing.discount.toInt()}", isDiscount = true)
                            }
                            SummaryRow("Estimated Shipping", "৳${pricing.shippingFee.toInt()}")

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = NovexaCardBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                                Text("৳${pricing.total.toInt()}", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = NovexaBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemWithProduct,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "৳${item.product.price.toInt()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NovexaBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NovexaBlueContainer)
                    ) {
                        IconButton(
                            onClick = { onQuantityChange(item.cartItem.quantity - 1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = NovexaBlue, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "${item.cartItem.quantity}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NovexaBlue,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        IconButton(
                            onClick = { onQuantityChange(item.cartItem.quantity + 1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = NovexaBlue, modifier = Modifier.size(16.dp))
                        }
                    }

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = NovexaAccentRose)
                    }
                }
            }
        }
    }
}
