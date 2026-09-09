package com.example.novexa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.example.novexa.ui.components.NovexaHeader
import com.example.novexa.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.novexa.ui.viewmodel.NovexaViewModel
import com.example.novexa.ui.viewmodel.ProductSortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsSearchScreen(
    viewModel: NovexaViewModel,
    onProductClick: (Long) -> Unit,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val wishlistedIds = remember(wishlist) { wishlist.map { it.id }.toSet() }

    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                NovexaHeader(
                    title = "Products & Search",
                    showBack = true,
                    onBack = onBack,
                    cartItemCount = cartItems.sumOf { it.cartItem.quantity },
                    wishlistCount = wishlist.size,
                    onCartClick = onCartClick,
                    onWishlistClick = onWishlistClick,
                    onAdminClick = onAdminClick
                )

                // Search Input Field
                OutlinedTextField(
                    value = filterState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search by name, SKU, brand, tags...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = NovexaBlue)
                    },
                    trailingIcon = {
                        if (filterState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = NovexaBlue,
                        unfocusedBorderColor = NovexaCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("search_text_input")
                )

                // Category Chips Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filterState.categoryId == null,
                            onClick = { viewModel.selectCategory(null) },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NovexaBlack,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(categories) { cat ->
                        FilterChip(
                            selected = filterState.categoryId == cat.id,
                            onClick = { viewModel.selectCategory(cat.id) },
                            label = { Text(cat.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NovexaBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Filter & Sort Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredProducts.size} Products found",
                        style = MaterialTheme.typography.bodySmall,
                        color = NovexaTextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showSortMenu = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_sort_options")
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(filterState.sortOption.displayName, fontSize = 12.sp)
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            ProductSortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName) },
                                    onClick = {
                                        viewModel.setSortOption(option)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (filterState.sortOption == option) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = NovexaBlue)
                                        }
                                    }
                                )
                            }
                        }

                        Button(
                            onClick = { showFilterSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_filter_dialog")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Filters", fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (filteredProducts.isEmpty()) {
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
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        tint = NovexaTextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Products Found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NovexaBlack
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try adjusting your search terms or filters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NovexaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.resetFilters() },
                        colors = ButtonDefaults.buttonColors(containerColor = NovexaBlack)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onAddToCart = { viewModel.addToCart(product.id) },
                        onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                        isWishlisted = wishlistedIds.contains(product.id)
                    )
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Filter Products",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NovexaBlack
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Price Range
                Text(
                    text = "Price Range: ৳${filterState.minPrice.toInt()} - ৳${filterState.maxPrice.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                RangeSlider(
                    value = filterState.minPrice.toFloat()..filterState.maxPrice.toFloat(),
                    onValueChange = { range ->
                        viewModel.setPriceRange(range.start.toDouble(), range.endInclusive.toDouble())
                    },
                    valueRange = 0f..25000f,
                    steps = 25,
                    colors = SliderDefaults.colors(
                        thumbColor = NovexaBlue,
                        activeTrackColor = NovexaBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetFilters()
                            showFilterSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset")
                    }
                    Button(
                        onClick = { showFilterSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
