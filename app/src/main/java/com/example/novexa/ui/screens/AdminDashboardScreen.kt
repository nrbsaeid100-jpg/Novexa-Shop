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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.novexa.data.local.entity.OrderEntity
import com.example.novexa.data.local.entity.ProductEntity
import com.example.novexa.ui.components.StockBadge
import com.example.ui.theme.*
import com.example.novexa.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val analytics by adminViewModel.analyticsSummary.collectAsState()
    val orders by adminViewModel.allOrders.collectAsState()
    val products by adminViewModel.allProducts.collectAsState()
    val lowStockProducts by adminViewModel.lowStockProducts.collectAsState()
    val transactions by adminViewModel.inventoryTransactions.collectAsState()
    val coupons by adminViewModel.allCoupons.collectAsState()
    val adminMessage by adminViewModel.adminMessage.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Orders, 2: Products & Stock, 3: Inventory Audit, 4: Coupons
    var showAdjustStockDialog by remember { mutableStateOf<ProductEntity?>(null) }
    var showNewProductDialog by remember { mutableStateOf(false) }
    var showNewCouponDialog by remember { mutableStateOf(false) }
    var showOrderStatusDialog by remember { mutableStateOf<OrderEntity?>(null) }

    Scaffold(
        topBar = {
            Surface(color = NovexaBlack, shadowElevation = 3.dp) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_admin_back")) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Column {
                                Text("Novexa Admin Console", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Dhaka Operations Center", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            }
                        }

                        IconButton(onClick = { adminViewModel.refreshAnalytics() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                        }
                    }

                    // Navigation Tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = NovexaBlack,
                        contentColor = NovexaBlueLight,
                        edgePadding = 12.dp
                    ) {
                        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Overview", fontSize = 12.sp) })
                        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Orders (${orders.size})", fontSize = 12.sp) })
                        Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Products & Stock", fontSize = 12.sp) })
                        Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Stock Audit", fontSize = 12.sp) })
                        Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Coupons", fontSize = 12.sp) })
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Flash banner if message
            if (adminMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NovexaBlueContainer),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(adminMessage!!, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = NovexaBlue)
                            IconButton(onClick = { adminViewModel.clearAdminMessage() }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = NovexaBlue)
                            }
                        }
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    // --- OVERVIEW & ANALYTICS ---
                    item {
                        Text("Business Performance Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                    }

                    // Top Revenue Cards
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(
                                title = "Total Revenue",
                                value = "৳${analytics?.totalSales?.toInt() ?: 0}",
                                subtitle = "Lifetime sales",
                                modifier = Modifier.weight(1f),
                                accent = NovexaBlue
                            )
                            MetricCard(
                                title = "Today's Sales",
                                value = "৳${analytics?.todaySales?.toInt() ?: 0}",
                                subtitle = "Orders today",
                                modifier = Modifier.weight(1f),
                                accent = NovexaAccentEmerald
                            )
                        }
                    }

                    // Secondary Cards
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(
                                title = "Total Orders",
                                value = "${analytics?.totalOrders ?: 0}",
                                subtitle = "${analytics?.pendingOrders ?: 0} pending",
                                modifier = Modifier.weight(1f),
                                accent = NovexaAccentAmber
                            )
                            MetricCard(
                                title = "Average Order",
                                value = "৳${analytics?.averageOrderValue?.toInt() ?: 0}",
                                subtitle = "Per transaction",
                                modifier = Modifier.weight(1f),
                                accent = NovexaBlack
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(
                                title = "Catalog Valuation",
                                value = "৳${analytics?.inventoryValuation?.toInt() ?: 0}",
                                subtitle = "Stock cost basis",
                                modifier = Modifier.weight(1f),
                                accent = NovexaTextSecondary
                            )
                            MetricCard(
                                title = "Est. Gross Profit",
                                value = "৳${analytics?.grossProfit?.toInt() ?: 0}",
                                subtitle = "On existing stock",
                                modifier = Modifier.weight(1f),
                                accent = NovexaAccentEmerald
                            )
                        }
                    }

                    // Low Stock Alerts
                    if (lowStockProducts.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = NovexaAccentAmber)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Low Stock Attention Needed (${lowStockProducts.size} items)",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    lowStockProducts.forEach { p ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(p.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                            Text("${p.stockQuantity} units left", fontWeight = FontWeight.Bold, color = NovexaAccentRose, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // --- ORDERS MANAGEMENT ---
                    item {
                        Text("Order Fulfillment & Status Control", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                        Text("Tap any order to update lifecycle (Pending → Confirmed → Processing → Shipped → Delivered → Cancelled).", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                    }

                    items(orders) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showOrderStatusDialog = order },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("#${order.orderNumber}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                        Text(order.customerName, style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                                    }
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

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Amount: ৳${order.total.toInt()} (${order.paymentMethod})", style = MaterialTheme.typography.bodySmall)
                                    Text("Destination: ${order.district}", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text("Change Status ⚙️", fontWeight = FontWeight.Bold, color = NovexaBlue, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // --- PRODUCTS & STOCK ---
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Product Catalog & Inventory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                                Text("${products.size} Products configured", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                            }
                            Button(
                                onClick = { showNewProductDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Product")
                            }
                        }
                    }

                    items(products) { prod ->
                        Card(
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
                                    Text(prod.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("SKU: ${prod.sku} • Price: ৳${prod.price.toInt()} (Cost: ৳${prod.costPrice.toInt()})", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        StockBadge(status = prod.stockStatus)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${prod.stockQuantity} in stock", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Button(
                                    onClick = { showAdjustStockDialog = prod },
                                    colors = ButtonDefaults.buttonColors(containerColor = NovexaBlack),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Adjust", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // --- STOCK AUDIT LOGS ---
                    item {
                        Text("Inventory Ledger Audit Trail", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                        Text("Auditable transaction log of all sales, restocks, and manual adjustments.", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                    }

                    items(transactions.reversed()) { tx ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(tx.productName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = if (tx.quantityChange > 0) "+${tx.quantityChange}" else "${tx.quantityChange}",
                                        fontWeight = FontWeight.Black,
                                        color = if (tx.quantityChange > 0) NovexaAccentEmerald else NovexaAccentRose
                                    )
                                }
                                Text("Type: ${tx.type} • Stock: ${tx.previousStock} → ${tx.newStock}", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                                if (!tx.referenceNote.isNullOrBlank()) {
                                    Text("Note: ${tx.referenceNote}", style = MaterialTheme.typography.labelSmall, color = NovexaTextMuted)
                                }
                                Text(
                                    text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(tx.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NovexaTextMuted
                                )
                            }
                        }
                    }
                }

                4 -> {
                    // --- COUPONS ---
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Promotional Coupons", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                                Text("Manage discount codes for Bangladeshi shoppers", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                            }
                            Button(
                                onClick = { showNewCouponDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Coupon")
                            }
                        }
                    }

                    items(coupons) { coupon ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(coupon.code, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = NovexaBlue)
                                    Text(if (coupon.isActive) "ACTIVE" else "EXPIRED", fontWeight = FontWeight.Bold, color = if (coupon.isActive) NovexaAccentEmerald else NovexaTextMuted, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Type: ${coupon.type} • Value: ${if (coupon.type == "PERCENTAGE") "${coupon.value.toInt()}%" else "৳${coupon.value.toInt()}"}", style = MaterialTheme.typography.bodySmall)
                                Text("Min Order: ৳${coupon.minOrder.toInt()} • Max Discount: ৳${coupon.maxDiscount.toInt()}", style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }

    // Status Transition Dialog
    if (showOrderStatusDialog != null) {
        val targetOrder = showOrderStatusDialog!!
        val validStatuses = listOf("Confirmed", "Processing", "Shipped", "Delivered", "Cancelled")

        AlertDialog(
            onDismissRequest = { showOrderStatusDialog = null },
            title = { Text("Update Order #${targetOrder.orderNumber}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select new status:")
                    validStatuses.forEach { status ->
                        Button(
                            onClick = {
                                adminViewModel.updateOrderStatus(targetOrder.id, status)
                                showOrderStatusDialog = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (status) {
                                    "Delivered" -> NovexaAccentEmerald
                                    "Cancelled" -> NovexaAccentRose
                                    targetOrder.orderStatus -> NovexaBlue
                                    else -> NovexaBlack
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(status)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showOrderStatusDialog = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Adjust Stock Dialog
    if (showAdjustStockDialog != null) {
        val targetProduct = showAdjustStockDialog!!
        var adjustAmount by remember { mutableStateOf("10") }
        var isAddition by remember { mutableStateOf(true) }
        var note by remember { mutableStateOf("Restock shipment arrival") }

        AlertDialog(
            onDismissRequest = { showAdjustStockDialog = null },
            title = { Text("Adjust Stock: ${targetProduct.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Current stock: ${targetProduct.stockQuantity} units")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = isAddition,
                            onClick = { isAddition = true },
                            label = { Text("+ Add Stock") }
                        )
                        FilterChip(
                            selected = !isAddition,
                            onClick = { isAddition = false },
                            label = { Text("- Deduct Stock") }
                        )
                    }
                    OutlinedTextField(
                        value = adjustAmount,
                        onValueChange = { adjustAmount = it },
                        label = { Text("Quantity") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Reason / Note") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = (adjustAmount.toIntOrNull() ?: 0) * (if (isAddition) 1 else -1)
                        adminViewModel.adjustStock(
                            productId = targetProduct.id,
                            quantityChange = qty,
                            type = if (isAddition) "PURCHASE" else "MANUAL_ADJUSTMENT",
                            note = note
                        )
                        showAdjustStockDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue)
                ) {
                    Text("Save Adjustment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdjustStockDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // New Product Dialog
    if (showNewProductDialog) {
        var pName by remember { mutableStateOf("") }
        var pBrand by remember { mutableStateOf("Novexa") }
        var pSku by remember { mutableStateOf("NVX-GEN-${(100..999).random()}") }
        var pPrice by remember { mutableStateOf("1500") }
        var pCostPrice by remember { mutableStateOf("950") }
        var pStock by remember { mutableStateOf("25") }

        AlertDialog(
            onDismissRequest = { showNewProductDialog = false },
            title = { Text("Add New Product to Catalog", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = pName, onValueChange = { pName = it }, label = { Text("Product Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pBrand, onValueChange = { pBrand = it }, label = { Text("Brand") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pSku, onValueChange = { pSku = it }, label = { Text("SKU *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pPrice, onValueChange = { pPrice = it }, label = { Text("Selling Price (৳) *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pCostPrice, onValueChange = { pCostPrice = it }, label = { Text("Cost Price (৳)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pStock, onValueChange = { pStock = it }, label = { Text("Initial Stock *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pName.isNotBlank()) {
                            val newProduct = ProductEntity(
                                name = pName,
                                sku = pSku,
                                slug = pName.lowercase().replace(" ", "-"),
                                categoryId = 1,
                                brand = pBrand,
                                description = "Authentic $pName officially imported by Novexa.",
                                shortDescription = pName,
                                price = pPrice.toDoubleOrNull() ?: 1500.0,
                                compareAtPrice = (pPrice.toDoubleOrNull() ?: 1500.0) * 1.2,
                                costPrice = pCostPrice.toDoubleOrNull() ?: 950.0,
                                stockQuantity = pStock.toIntOrNull() ?: 25,
                                imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80"
                            )
                            adminViewModel.saveProduct(newProduct, isNew = true)
                            showNewProductDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue)
                ) {
                    Text("Create Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProductDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // New Coupon Dialog
    if (showNewCouponDialog) {
        var cCode by remember { mutableStateOf("") }
        var cType by remember { mutableStateOf("PERCENTAGE") }
        var cValue by remember { mutableStateOf("10") }
        var cMinOrder by remember { mutableStateOf("1000") }
        var cMaxDiscount by remember { mutableStateOf("300") }

        AlertDialog(
            onDismissRequest = { showNewCouponDialog = false },
            title = { Text("Create Discount Coupon", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = cCode, onValueChange = { cCode = it.uppercase() }, label = { Text("Coupon Code (e.g. BD2026)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(selected = cType == "PERCENTAGE", onClick = { cType = "PERCENTAGE" }, label = { Text("% Off") })
                        FilterChip(selected = cType == "FIXED", onClick = { cType = "FIXED" }, label = { Text("Fixed ৳") })
                    }
                    OutlinedTextField(value = cValue, onValueChange = { cValue = it }, label = { Text("Discount Value") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = cMinOrder, onValueChange = { cMinOrder = it }, label = { Text("Min Order (৳)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = cMaxDiscount, onValueChange = { cMaxDiscount = it }, label = { Text("Max Discount Cap (৳)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cCode.isNotBlank()) {
                            adminViewModel.createCoupon(
                                code = cCode,
                                type = cType,
                                value = cValue.toDoubleOrNull() ?: 10.0,
                                minOrder = cMinOrder.toDoubleOrNull() ?: 1000.0,
                                maxDiscount = cMaxDiscount.toDoubleOrNull() ?: 300.0
                            )
                            showNewCouponDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewCouponDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    accent: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = NovexaTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = accent)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = NovexaTextMuted)
        }
    }
}
