package com.example.novexa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoPageScreen(
    title: String,
    type: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Surface(
                color = NovexaBlueDark,
                shadowElevation = 3.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NovexaHeaderGradient)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("btn_info_back")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
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
            when (type) {
                "ABOUT" -> {
                    item {
                        InfoCard(
                            heading = "About Novexa Bangladesh",
                            content = "Novexa is Bangladesh's premier online multi-category destination, engineered to bring modern authenticity, fast delivery, and trustworthy service across all 64 districts.\n\n" +
                                    "Founded with a vision to redefine e-commerce in Bangladesh, Novexa directly partners with verified manufacturers and brands across Electronics, Traditional & Modern Fashion, Cosmetics, Home appliances, and Daily Essentials.\n\n" +
                                    "With our central fulfillment warehouse in Dhaka and automated real-time inventory management, every order is processed with 100% genuine quality guarantees and transparent pricing."
                        )
                    }
                    item {
                        InfoCard(
                            heading = "Our Core Promises",
                            content = "• 100% Authentic Products: No counterfeit or grey-market goods.\n" +
                                    "• Swift Nationwide Delivery: Express same-day delivery in Dhaka; 48 hours to all upazilas across Bangladesh.\n" +
                                    "• Flexible Payment Choices: Seamless bKash, Nagad, DBBL Rocket MFS gateway, and Cash on Delivery.\n" +
                                    "• Customer-First Support: Dedicated 7-days-a-week helpline and WhatsApp concierge."
                        )
                    }
                }

                "CONTACT" -> {
                    item {
                        InfoCard(
                            heading = "Novexa Headquarters & Support",
                            content = "We are here to assist you with order inquiries, product guidance, and corporate partnerships."
                        )
                    }
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                ContactRow(Icons.Default.Phone, "Helpline", "+880 1811-223344 / +880 1712-345678")
                                ContactRow(Icons.Default.Email, "Email", "support@novexa.com.bd")
                                ContactRow(Icons.Default.LocationOn, "Corporate Office", "House 24, Road 71, Block D, Gulshan-2, Dhaka 1212")
                                ContactRow(Icons.Default.AccessTime, "Operating Hours", "9:00 AM - 10:00 PM, Every Day")
                            }
                        }
                    }
                }

                "FAQ" -> {
                    item {
                        InfoCard(
                            heading = "Frequently Asked Questions",
                            content = "Quick answers to common questions about shopping on Novexa."
                        )
                    }
                    item {
                        FaqItem("How much is shipping in Bangladesh?", "Delivery inside Dhaka is ৳60 (usually same day or within 24 hours). Delivery outside Dhaka across all 64 districts is ৳120 (48 to 72 hours). Orders over ৳2,000 qualify for free delivery with code FREESHIP.")
                    }
                    item {
                        FaqItem("What payment methods are supported?", "You can pay via Cash on Delivery (COD) upon receiving your parcel, or pay instantly using bKash, Nagad, or Rocket mobile financial services.")
                    }
                    item {
                        FaqItem("Can I inspect the parcel before paying for COD?", "Yes! You may check the parcel exterior and contents in the presence of the delivery agent before paying.")
                    }
                    item {
                        FaqItem("How do I cancel an order?", "You can cancel any order directly from the My Orders or Order Tracking screen while it is in 'Confirmed' or 'Processing' status. Reserved inventory is immediately returned to our catalog.")
                    }
                }

                "RETURN" -> {
                    item {
                        InfoCard(
                            heading = "7-Day Return & Replacement Policy",
                            content = "At Novexa, we stand firmly behind the quality of our products. If you receive a damaged, defective, or incorrect item:\n\n" +
                                    "1. Report within 7 days of delivery via our helpline or app.\n" +
                                    "2. Ensure original packaging and tags are intact.\n" +
                                    "3. Our courier will pick up the return from your address.\n" +
                                    "4. Replacement is dispatched within 24 hours of inspection, or an instant refund is sent directly to your bKash / Nagad wallet."
                        )
                    }
                }

                else -> {
                    item {
                        InfoCard(
                            heading = "Terms of Service & Privacy Policy",
                            content = "Novexa Bangladesh operates under the e-Commerce Guidelines 2021 issued by the Ministry of Commerce of Bangladesh.\n\n" +
                                    "All customer personal data, phone numbers, and addresses are strictly encrypted and never shared with third parties. All financial transactions via bKash and Nagad are processed through verified financial APIs with multi-factor PIN verification."
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(heading: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = heading, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = content, style = MaterialTheme.typography.bodyMedium, color = NovexaTextSecondary, lineHeight = 22.sp)
        }
    }
}

@Composable
fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(NovexaBlueContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = NovexaBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = NovexaTextMuted)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = NovexaBlack)
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = question, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = NovexaBlue)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = answer, style = MaterialTheme.typography.bodySmall, color = NovexaTextSecondary, lineHeight = 20.sp)
        }
    }
}
