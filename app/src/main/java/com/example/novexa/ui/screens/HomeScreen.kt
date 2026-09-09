package com.example.novexa.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.novexa.data.local.entity.CategoryEntity
import com.example.novexa.data.local.entity.ProductEntity
import com.example.novexa.ui.components.*
import com.example.ui.theme.*
import com.example.novexa.ui.viewmodel.NovexaViewModel

@Composable
fun HomeScreen(
    viewModel: NovexaViewModel,
    onProductClick: (Long) -> Unit,
    onCategoryClick: (Long) -> Unit,
    onViewAllProducts: () -> Unit,
    onOpenInfoPage: (String, String) -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onAdminClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val featuredProducts by viewModel.featuredProducts.collectAsState()
    val flashSaleProducts by viewModel.flashSaleProducts.collectAsState()
    val bestsellers by viewModel.bestsellers.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val wishlistedIds = remember(wishlist) { wishlist.map { it.id }.toSet() }

    var newsletterEmail by remember { mutableStateOf("") }
    var newsletterSubscribed by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                AnnouncementBar()
                NovexaHeader(
                    cartItemCount = cartItems.sumOf { it.cartItem.quantity },
                    wishlistCount = wishlist.size,
                    onSearchClick = onSearchClick,
                    onCartClick = onCartClick,
                    onWishlistClick = onWishlistClick,
                    onAdminClick = onAdminClick
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Search trigger banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onSearchClick() }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = NovexaTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Search Samsung, Panjabi, Saffron, Honey...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NovexaTextMuted
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = NovexaBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. Hero Promotional Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner),
                            contentDescription = "Novexa Grand Sale",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            NovexaBlack.copy(alpha = 0.85f),
                                            NovexaBlueDark.copy(alpha = 0.65f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NovexaAccentRose)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "EID MEGA DEALS 2026",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Up To 50% Off\nAcross Bangladesh",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onViewAllProducts,
                                colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Shop Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // 3. Featured Categories Horizontal Scroll
            item {
                SectionHeader(title = "Featured Categories", subtitle = "Explore verified collections")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { cat ->
                        CategoryItem(category = cat, onClick = { onCategoryClick(cat.id) })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Flash Sale Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF881337), Color(0xFFE11D48))
                            )
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = NovexaAccentAmber, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("FLASH SALE", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.White)
                                Text("Limited Stock & Time", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFFD1D9))
                            }
                        }
                        // Countdown timer pills
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            CountdownBadge("06", "HRS")
                            CountdownBadge("42", "MIN")
                            CountdownBadge("18", "SEC")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(flashSaleProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { viewModel.addToCart(product.id) },
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            isWishlisted = wishlistedIds.contains(product.id),
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            }

            // 5. Best-Selling Products Grid
            item {
                SectionHeader(
                    title = "Best-Selling in BD",
                    subtitle = "Customer favorites this week",
                    onViewAllClick = onViewAllProducts
                )
            }

            items(bestsellers.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (prod in pair) {
                        ProductCard(
                            product = prod,
                            onClick = { onProductClick(prod.id) },
                            onAddToCart = { viewModel.addToCart(prod.id) },
                            onWishlistToggle = { viewModel.toggleWishlist(prod.id) },
                            isWishlisted = wishlistedIds.contains(prod.id),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // 6. Featured Products Row
            item {
                SectionHeader(
                    title = "New Arrivals & Trending",
                    subtitle = "Handpicked for modern lifestyles",
                    onViewAllClick = onViewAllProducts
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(featuredProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { viewModel.addToCart(product.id) },
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            isWishlisted = wishlistedIds.contains(product.id),
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            }

            // 7. Why Shop With Novexa (Trust Signals for Bangladesh)
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Why Shop with Novexa?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NovexaBlack
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        TrustFeatureItem(
                            icon = Icons.Default.LocalShipping,
                            title = "Express Delivery Across BD",
                            desc = "Same-day inside Dhaka & 48h to all 64 districts"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = NovexaCardBorder)
                        TrustFeatureItem(
                            icon = Icons.Default.Verified,
                            title = "100% Genuine & Authentic",
                            desc = "Direct manufacturer sourcing with warranty"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = NovexaCardBorder)
                        TrustFeatureItem(
                            icon = Icons.Default.CurrencyExchange,
                            title = "bKash, Nagad & Cash on Delivery",
                            desc = "Pay securely via your preferred MFS or at doorstep"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = NovexaCardBorder)
                        TrustFeatureItem(
                            icon = Icons.Default.AssignmentReturn,
                            title = "7-Day Easy Return Policy",
                            desc = "Hassle-free replacement guarantee for damaged items"
                        )
                    }
                }
            }

            // 8. Newsletter Subscription
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(NovexaBlueDark, NovexaBlack)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Join Novexa Insider Club",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Get exclusive flash discounts, coupon codes and early access to Eid sales.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        if (newsletterSubscribed) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NovexaAccentEmerald)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Thank you for subscribing! Check your inbox for ৳100 off code.",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newsletterEmail,
                                    onValueChange = { newsletterEmail = it },
                                    placeholder = { Text("Enter your email...", fontSize = 13.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_newsletter_email"),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedTextColor = NovexaBlack,
                                        unfocusedTextColor = NovexaBlack
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newsletterEmail.isNotBlank()) newsletterSubscribed = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NovexaBlueLight),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("btn_subscribe_newsletter")
                                ) {
                                    Text("Join", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 9. Footer & Policies
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NOVEXA BANGLADESH",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = NovexaBlack
                    )
                    Text(
                        text = "Online Multi-Category Store • Dhaka, Bangladesh",
                        style = MaterialTheme.typography.bodySmall,
                        color = NovexaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { onOpenInfoPage("About Us", "ABOUT") }) {
                            Text("About Us", style = MaterialTheme.typography.labelSmall, color = NovexaTextSecondary)
                        }
                        TextButton(onClick = { onOpenInfoPage("Contact Us", "CONTACT") }) {
                            Text("Contact", style = MaterialTheme.typography.labelSmall, color = NovexaTextSecondary)
                        }
                        TextButton(onClick = { onOpenInfoPage("FAQ", "FAQ") }) {
                            Text("FAQ", style = MaterialTheme.typography.labelSmall, color = NovexaTextSecondary)
                        }
                        TextButton(onClick = { onOpenInfoPage("Return & Refund", "RETURN") }) {
                            Text("Returns", style = MaterialTheme.typography.labelSmall, color = NovexaTextSecondary)
                        }
                    }
                    Text(
                        text = "Accepted MFS & Cards: bKash | Nagad | Rocket | Visa | Mastercard | COD",
                        style = MaterialTheme.typography.labelSmall,
                        color = NovexaTextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "© 2026 Novexa BD Ltd. All rights reserved.",
                        style = MaterialTheme.typography.labelSmall,
                        color = NovexaTextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: CategoryEntity,
    onClick: () -> Unit
) {
    val icon = when (category.iconName) {
        "Devices" -> Icons.Default.Devices
        "Checkroom" -> Icons.Default.Checkroom
        "Spa" -> Icons.Default.Spa
        "Kitchen" -> Icons.Default.SoupKitchen
        "ShoppingBasket" -> Icons.Default.ShoppingBasket
        else -> Icons.Default.CardGiftcard
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(6.dp)
            .width(76.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(NovexaBlueContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category.name,
                tint = NovexaBlue,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = NovexaTextPrimary
        )
    }
}

@Composable
fun CountdownBadge(number: String, unit: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(number, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
        Text(unit, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.8f), fontSize = 8.sp)
    }
}

@Composable
fun TrustFeatureItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NovexaBlueContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NovexaBlue, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NovexaTextPrimary)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
        }
    }
}
