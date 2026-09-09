package com.example.novexa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.novexa.ui.components.SummaryRow
import com.example.ui.theme.*
import com.example.novexa.ui.viewmodel.NovexaViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    order: OrderEntity,
    viewModel: NovexaViewModel,
    onBack: () -> Unit,
    onContinueShopping: () -> Unit
) {
    val userOrders by viewModel.userOrders.collectAsState()
    val liveOrder = remember(userOrders, order.id) {
        userOrders.find { it.id == order.id } ?: order
    }

    var showCancelDialog by remember { mutableStateOf(false) }

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
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_tracking_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NovexaBlack)
                    }
                    Text(
                        text = "Order #${liveOrder.orderNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NovexaBlack
                    )
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
            // 1. Status Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (liveOrder.orderStatus) {
                            "Cancelled" -> Color(0xFFFFE4E6)
                            "Delivered" -> Color(0xFFDCFCE7)
                            else -> NovexaBlueContainer
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    when (liveOrder.orderStatus) {
                                        "Cancelled" -> NovexaAccentRose
                                        "Delivered" -> NovexaAccentEmerald
                                        else -> NovexaBlue
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (liveOrder.orderStatus) {
                                    "Cancelled" -> Icons.Default.Cancel
                                    "Delivered" -> Icons.Default.CheckCircle
                                    "Shipped" -> Icons.Default.LocalShipping
                                    else -> Icons.Default.Inventory2
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Status: ${liveOrder.orderStatus}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = NovexaBlack
                            )
                            Text(
                                text = "Placed on ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(liveOrder.createdAt))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NovexaTextSecondary
                            )
                        }
                    }
                }
            }

            // 2. Timeline Stepper
            if (liveOrder.orderStatus != "Cancelled") {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Tracking Timeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                            Spacer(modifier = Modifier.height(14.dp))

                            val steps = listOf("Confirmed", "Processing", "Shipped", "Delivered")
                            val currentStepIndex = steps.indexOf(liveOrder.orderStatus).let { if (it == -1) 0 else it }

                            steps.forEachIndexed { index, step ->
                                val isPassed = index <= currentStepIndex
                                val isCurrent = index == currentStepIndex

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (isPassed) NovexaBlue else Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isPassed) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = step,
                                            fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Medium,
                                            color = if (isPassed) NovexaBlack else NovexaTextMuted,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = when (step) {
                                                "Confirmed" -> "Order placed & verified by Novexa system"
                                                "Processing" -> "Item packaged in central Dhaka fulfillment center"
                                                "Shipped" -> "Handed to courier for express doorstep delivery"
                                                else -> "Package successfully received by customer"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NovexaTextSecondary
                                        )
                                    }
                                }
                                if (index < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 11.dp)
                                            .width(2.dp)
                                            .height(24.dp)
                                            .background(if (index < currentStepIndex) NovexaBlue else Color.LightGray)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Payment & Delivery Info
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Payment Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                        Spacer(modifier = Modifier.height(10.dp))

                        SummaryRow("Payment Method", liveOrder.paymentMethod)
                        SummaryRow("Payment Status", liveOrder.paymentStatus)
                        if (!liveOrder.paymentTransactionId.isNullOrBlank()) {
                            SummaryRow("Transaction ID", liveOrder.paymentTransactionId!!)
                        }
                        SummaryRow("Total Paid/Due", "৳${liveOrder.total.toInt()}")

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = NovexaCardBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Delivery Destination", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(liveOrder.customerName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(liveOrder.customerPhone, style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary)
                        Text("${liveOrder.fullAddress}, ${liveOrder.upazila}, ${liveOrder.district}, ${liveOrder.division}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // 4. Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (liveOrder.orderStatus == "Confirmed" || liveOrder.orderStatus == "Processing") {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NovexaAccentRose),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_cancel_order")
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cancel Order & Restore Stock", fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onContinueShopping,
                        colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_order_done_shopping")
                    ) {
                        Text("Continue Shopping", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Cancel Order Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Order?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to cancel order #${liveOrder.orderNumber}? The reserved inventory will be automatically restored to the catalog.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelOrder(liveOrder.id, "Customer requested cancellation")
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovexaAccentRose)
                ) {
                    Text("Confirm Cancellation")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Order")
                }
            }
        )
    }
}
