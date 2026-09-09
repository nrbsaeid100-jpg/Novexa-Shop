package com.example.novexa.payment

sealed class PaymentResult {
    data class Success(val transactionId: String, val provider: String, val message: String) : PaymentResult()
    data class Failed(val errorCode: String, val errorMessage: String) : PaymentResult()
    data class Cancelled(val reason: String) : PaymentResult()
}

data class PaymentRequest(
    val orderNumber: String,
    val amount: Double,
    val currency: String = "BDT",
    val customerPhone: String,
    val paymentMethod: String // "bKash", "Nagad", "Rocket", "COD", "Online"
)

interface PaymentProvider {
    val providerName: String
    suspend fun initiatePayment(request: PaymentRequest): String // returns payment redirect / session ID
    suspend fun executePayment(sessionId: String, authCodeOrPin: String, request: PaymentRequest): PaymentResult
    suspend fun verifyWebhook(signature: String, payload: String): Boolean
}

class BkashPaymentProvider(
    private val appKey: String = "ENV_BKASH_APP_KEY",
    private val appSecret: String = "ENV_BKASH_APP_SECRET"
) : PaymentProvider {
    override val providerName: String = "bKash"

    override suspend fun initiatePayment(request: PaymentRequest): String {
        return "bkash_sess_${System.currentTimeMillis()}"
    }

    override suspend fun executePayment(sessionId: String, authCodeOrPin: String, request: PaymentRequest): PaymentResult {
        if (authCodeOrPin.length < 4) {
            return PaymentResult.Failed("INVALID_PIN", "bKash security PIN must be at least 4 digits")
        }
        val trxId = "BK-${(10000000..99999999).random()}"
        return PaymentResult.Success(trxId, providerName, "bKash payment of ৳${request.amount} successful")
    }

    override suspend fun verifyWebhook(signature: String, payload: String): Boolean = true
}

class NagadPaymentProvider : PaymentProvider {
    override val providerName: String = "Nagad"

    override suspend fun initiatePayment(request: PaymentRequest): String {
        return "nagad_sess_${System.currentTimeMillis()}"
    }

    override suspend fun executePayment(sessionId: String, authCodeOrPin: String, request: PaymentRequest): PaymentResult {
        if (authCodeOrPin.length < 4) {
            return PaymentResult.Failed("INVALID_PIN", "Nagad PIN must be at least 4 digits")
        }
        val trxId = "NG-${(10000000..99999999).random()}"
        return PaymentResult.Success(trxId, providerName, "Nagad payment of ৳${request.amount} successful")
    }

    override suspend fun verifyWebhook(signature: String, payload: String): Boolean = true
}

class RocketPaymentProvider : PaymentProvider {
    override val providerName: String = "Rocket"

    override suspend fun initiatePayment(request: PaymentRequest): String {
        return "rocket_sess_${System.currentTimeMillis()}"
    }

    override suspend fun executePayment(sessionId: String, authCodeOrPin: String, request: PaymentRequest): PaymentResult {
        if (authCodeOrPin.length < 4) {
            return PaymentResult.Failed("INVALID_PIN", "Rocket PIN must be at least 4 digits")
        }
        val trxId = "RK-${(10000000..99999999).random()}"
        return PaymentResult.Success(trxId, providerName, "Rocket payment of ৳${request.amount} successful")
    }

    override suspend fun verifyWebhook(signature: String, payload: String): Boolean = true
}

class CashOnDeliveryProvider : PaymentProvider {
    override val providerName: String = "COD"

    override suspend fun initiatePayment(request: PaymentRequest): String = "cod_sess"

    override suspend fun executePayment(sessionId: String, authCodeOrPin: String, request: PaymentRequest): PaymentResult {
        return PaymentResult.Success("COD-${System.currentTimeMillis().toString().takeLast(6)}", providerName, "Cash on Delivery recorded")
    }

    override suspend fun verifyWebhook(signature: String, payload: String): Boolean = true
}

class PaymentGatewayService {
    private val providers = mapOf(
        "bKash" to BkashPaymentProvider(),
        "Nagad" to NagadPaymentProvider(),
        "Rocket" to RocketPaymentProvider(),
        "COD" to CashOnDeliveryProvider(),
        "Online" to BkashPaymentProvider()
    )

    fun getProvider(method: String): PaymentProvider {
        return providers[method] ?: providers["COD"]!!
    }
}
