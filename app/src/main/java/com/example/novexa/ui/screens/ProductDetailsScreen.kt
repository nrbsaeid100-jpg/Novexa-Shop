package com.example.novexa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.novexa.data.local.entity.ProductVariantEntity
import com.example.novexa.data.local.entity.ReviewEntity
import com.example.novexa.ui.components.StockBadge
import com.example.ui.theme.*
import com.example.novexa.ui.viewmodel.NovexaViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: Long,
    viewModel: NovexaViewModel,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val product = remember(allProducts, productId) {
        allProducts.find { it.id == productId }
    }

    val variants by remember(productId) {
        // Collect variants if any
        viewModel.categories // trigger recomposition
        mutableStateOf(emptyList<ProductVariantEntity>())
    }

    val wishlist by viewModel.wishlist.collectAsState()
    val isWishlisted = remember(wishlist, productId) { wishlist.any { it.id == productId } }
    val cartItems by viewModel.cartItems.collectAsState()
    val cartItemCount = remember(cartItems) { cartItems.sumOf { it.cartItem.quantity } }

    var selectedVariantId by remember { mutableStateOf<Long?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var showReviewDialog by remember { mutableStateOf(false) }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NovexaBlue)
        }
        return
    }

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_details_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NovexaBlack)
                    }

                    Text(
                        text = product.brand,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NovexaBlack
                    )

                    Row {
                        IconButton(onClick = { viewModel.toggleWishlist(product.id) }) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) NovexaAccentRose else NovexaBlack
                            )
                        }
                        IconButton(onClick = onNavigateToCart) {
                            BadgedBox(
                                badge = {
                                    if (cartItemCount > 0) {
                                        Badge(containerColor = NovexaBlue, contentColor = Color.White) {
                                            Text("$cartItemCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Outlined.ShoppingCart, contentDescription = "Cart", tint = NovexaBlack)
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Add to cart
                    OutlinedButton(
                        onClick = {
                            viewModel.addToCart(product.id, selectedVariantId, quantity)
                        },
                        enabled = product.stockQuantity > 0,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("btn_details_add_to_cart")
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = NovexaBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold, color = NovexaBlue)
                    }

                    // Buy now
                    Button(
                        onClick = {
                            viewModel.addToCart(product.id, selectedVariantId, quantity)
                            onNavigateToCheckout()
                        },
                        enabled = product.stockQuantity > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("btn_details_buy_now")
                    ) {
                        Text("Buy Now", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Product Image Hero Display
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.White)
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )

                    if (product.discountPercent > 0) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NovexaAccentRose)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "-${product.discountPercent}% OFF",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Product Meta & Pricing
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = product.brand,
                                style = MaterialTheme.typography.labelMedium,
                                color = NovexaBlue,
                                fontWeight = FontWeight.Bold
                            )
                            StockBadge(status = product.stockStatus)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NovexaBlack
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Rating row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = NovexaAccentAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "%.1f".format(product.rating),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NovexaBlack
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${product.reviewCount} customer reviews",
                                style = MaterialTheme.typography.bodySmall,
                                color = NovexaTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = NovexaCardBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Pricing Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "৳${product.price.toInt()}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = NovexaBlue
                                )
                                if (product.compareAtPrice > product.price) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "৳${product.compareAtPrice.toInt()}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textDecoration = TextDecoration.LineThrough,
                                            color = NovexaTextMuted
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Save ৳${(product.compareAtPrice - product.price).toInt()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NovexaAccentEmerald,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Quantity Stepper
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NovexaBlueContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { if (quantity > 1) quantity-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = NovexaBlue)
                                }
                                Text(
                                    text = "$quantity",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NovexaBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = { if (quantity < product.stockQuantity) quantity++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = NovexaBlue)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Bangladesh Delivery & Warranty Notice
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = NovexaBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delivery in Bangladesh", fontWeight = FontWeight.Bold, color = NovexaBlack)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Inside Dhaka: ৳60 (Same day or 24 Hours)", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                        Text("• Outside Dhaka: ৳120 (48-72 Hours to all Upazilas)", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                        Text("• Cash on Delivery & bKash/Nagad available", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                    }
                }
            }

            // 4. Description & Specifications
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Product Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NovexaBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = NovexaTextPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Specifications", fontWeight = FontWeight.Bold, color = NovexaBlack)
                        Spacer(modifier = Modifier.height(8.dp))

                        SpecRow("SKU", product.sku)
                        SpecRow("Brand", product.brand)
                        SpecRow("Weight", "${product.weight} kg")
                        SpecRow("Dimensions", product.dimensions)
                        SpecRow("Stock Left", "${product.stockQuantity} units")
                    }
                }
            }

            // 5. Customer Reviews Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Customer Reviews",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NovexaBlack
                            )
                            Button(
                                onClick = { showReviewDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_write_review")
                            ) {
                                Text("Write Review", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sample customer review card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NovexaBgLight),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Zubair Ahmed", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Row {
                                        repeat(5) {
                                            Icon(Icons.Filled.Star, contentDescription = null, tint = NovexaAccentAmber, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NovexaAccentEmerald, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verified Purchase (Dhaka)", fontSize = 10.sp, color = NovexaAccentEmerald)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Extremely satisfied with the authentic product packaging and fast doorstep delivery. Will definitely buy again from Novexa!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NovexaTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (showReviewDialog) {
        var userRating by remember { mutableStateOf(5) }
        var reviewText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Write a Review", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Your Rating:")
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { userRating = star }, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = if (star <= userRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "$star stars",
                                    tint = NovexaAccentAmber
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        placeholder = { Text("Write your honest feedback about this product...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reviewText.isNotBlank()) {
                            viewModel.submitReview(product.id, userRating, reviewText)
                            showReviewDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue)
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NovexaTextPrimary)
    }
}
