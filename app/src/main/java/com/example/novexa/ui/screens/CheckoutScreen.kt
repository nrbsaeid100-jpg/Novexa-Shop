package com.example.novexa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.novexa.data.local.entity.OrderEntity
import com.example.novexa.ui.components.SummaryRow
import com.example.ui.theme.*
import com.example.novexa.ui.viewmodel.NovexaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: NovexaViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (OrderEntity) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val pricing by viewModel.cartPricing.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val isInsideCity by viewModel.isInsideCity.collectAsState()

    var name by remember { mutableStateOf(currentUser?.name ?: "Rafiqul Islam") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "+8801712345678") }
    var email by remember { mutableStateOf(currentUser?.email ?: "customer@novexa.com.bd") }

    val divisions = listOf("Dhaka", "Chattogram", "Rajshahi", "Sylhet", "Khulna", "Barishal", "Rangpur", "Mymensingh")
    var selectedDivision by remember { mutableStateOf("Dhaka") }
    var district by remember { mutableStateOf("Dhaka") }
    var upazila by remember { mutableStateOf("Gulshan-2") }
    var fullAddress by remember { mutableStateOf("House 24, Road 71, Block D, Gulshan-2, Dhaka 1212") }
    var deliveryInstructions by remember { mutableStateOf("") }

    val paymentMethods = listOf("Cash on Delivery (COD)", "bKash", "Nagad", "Rocket")
    var selectedPaymentMethod by remember { mutableStateOf("bKash") }
    var mfsPin by remember { mutableStateOf("1234") }

    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_checkout_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NovexaBlack)
                    }
                    Text(
                        text = "Secure Checkout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NovexaBlack
                    )
                }
            }
        },
        bottomBar = {
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
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = NovexaAccentRose,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (name.isBlank() || phone.isBlank() || fullAddress.isBlank()) {
                                errorMessage = "Please fill in all required delivery fields."
                                return@Button
                            }
                            isProcessing = true
                            errorMessage = null
                            val cleanMethod = when {
                                selectedPaymentMethod.contains("bKash") -> "bKash"
                                selectedPaymentMethod.contains("Nagad") -> "Nagad"
                                selectedPaymentMethod.contains("Rocket") -> "Rocket"
                                else -> "COD"
                            }
                            viewModel.placeOrder(
                                fullName = name,
                                phone = phone,
                                email = email,
                                division = selectedDivision,
                                district = district,
                                upazila = upazila,
                                fullAddress = fullAddress,
                                deliveryInstructions = deliveryInstructions,
                                paymentMethod = cleanMethod,
                                paymentPin = if (cleanMethod == "COD") "0000" else mfsPin,
                                onSuccess = { order ->
                                    isProcessing = false
                                    onOrderPlaced(order)
                                },
                                onError = { err ->
                                    isProcessing = false
                                    errorMessage = err
                                }
                            )
                        },
                        enabled = !isProcessing && cartItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = NovexaBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_confirm_place_order")
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Place Order • ৳${pricing.total.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
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
            // Security badge
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(NovexaBlueContainer)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = NovexaBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100% Server-Side Verified Checkout & Authentic Guarantee",
                        style = MaterialTheme.typography.labelSmall,
                        color = NovexaBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Customer Contact Info
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Customer Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number (+880) *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Delivery Address (Bangladesh Divisions & Districts)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Delivery Address (Bangladesh)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Select Division:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ScrollableTabRow(
                                selectedTabIndex = divisions.indexOf(selectedDivision).coerceAtLeast(0),
                                edgePadding = 0.dp
                            ) {
                                divisions.forEach { div ->
                                    Tab(
                                        selected = selectedDivision == div,
                                        onClick = {
                                            selectedDivision = div
                                            viewModel.setShippingLocation(div == "Dhaka")
                                        },
                                        text = { Text(div, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = district,
                                onValueChange = { district = it },
                                label = { Text("District *") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = upazila,
                                onValueChange = { upazila = it },
                                label = { Text("Thana / Upazila *") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = fullAddress,
                            onValueChange = { fullAddress = it },
                            label = { Text("Street Address, House/Flat No *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deliveryInstructions,
                            onValueChange = { deliveryInstructions = it },
                            label = { Text("Delivery Note / Landmark (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Payment Method Selection
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Payment Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                        Spacer(modifier = Modifier.height(10.dp))

                        paymentMethods.forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedPaymentMethod = method }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == method,
                                    onClick = { selectedPaymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = NovexaBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = method,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = when {
                                            method.contains("COD") -> "Pay cash upon parcel arrival at your door"
                                            method.contains("bKash") -> "Fast & secure instant mobile checkout"
                                            method.contains("Nagad") -> "Instant transaction via Post Office MFS"
                                            else -> "DBBL Rocket mobile banking gateway"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NovexaTextSecondary
                                    )
                                }
                            }
                        }

                        // If MFS selected, show PIN simulation
                        if (!selectedPaymentMethod.contains("COD")) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedPaymentMethod.contains("bKash")) Color(0xFFFDE8E8) else NovexaBlueContainer
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Gateway Simulator: $selectedPaymentMethod Authentication",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NovexaBlack
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = mfsPin,
                                        onValueChange = { if (it.length <= 5) mfsPin = it },
                                        label = { Text("$selectedPaymentMethod PIN / Auth Code") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Text(
                                        text = "Payment provider abstraction executes securely with mock auth code.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NovexaTextSecondary,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Items Summary
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
                        Spacer(modifier = Modifier.height(8.dp))

                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.cartItem.quantity}x ${item.product.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "৳${(item.product.price * item.cartItem.quantity).toInt()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = NovexaCardBorder)

                        SummaryRow("Subtotal", "৳${pricing.subtotal.toInt()}")
                        if (pricing.discount > 0) {
                            SummaryRow("Discount", "-৳${pricing.discount.toInt()}", isDiscount = true)
                        }
                        SummaryRow("Delivery Fee (${if (isInsideCity) "Inside Dhaka" else "Outside Dhaka"})", "৳${pricing.shippingFee.toInt()}")

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = NovexaCardBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Payable", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                            Text("৳${pricing.total.toInt()}", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = NovexaBlue)
                        }
                    }
                }
            }
        }
    }
}
