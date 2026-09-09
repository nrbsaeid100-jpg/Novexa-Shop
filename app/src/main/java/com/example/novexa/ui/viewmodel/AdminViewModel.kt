package com.example.novexa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.novexa.data.local.entity.*
import com.example.novexa.data.repository.AdminAnalyticsSummary
import com.example.novexa.data.repository.NovexaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: NovexaRepository
) : ViewModel() {

    private val _analyticsSummary = MutableStateFlow<AdminAnalyticsSummary?>(null)
    val analyticsSummary: StateFlow<AdminAnalyticsSummary?> = _analyticsSummary.asStateFlow()

    val allOrders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<ProductEntity>> = repository.getLowStockProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCoupons: StateFlow<List<CouponEntity>> = repository.getAllCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventoryTransactions: StateFlow<List<InventoryTransactionEntity>> = repository.getAllInventoryTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _adminMessage = MutableStateFlow<String?>(null)
    val adminMessage: StateFlow<String?> = _adminMessage.asStateFlow()

    init {
        refreshAnalytics()
    }

    fun refreshAnalytics() {
        viewModelScope.launch {
            _analyticsSummary.value = repository.getAnalyticsSummary()
        }
    }

    fun clearAdminMessage() {
        _adminMessage.value = null
    }

    fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            val success = repository.updateOrderStatus(orderId, newStatus)
            if (success) {
                _adminMessage.value = "Order status updated to $newStatus"
                refreshAnalytics()
            } else {
                _adminMessage.value = "Failed to update order status"
            }
        }
    }

    fun adjustStock(productId: Long, quantityChange: Int, type: String, note: String) {
        viewModelScope.launch {
            val result = repository.adjustStock(productId, quantityChange, type, note)
            result.onSuccess {
                _adminMessage.value = "Stock updated successfully"
                refreshAnalytics()
            }.onFailure {
                _adminMessage.value = it.message ?: "Failed to adjust stock"
            }
        }
    }

    fun saveProduct(product: ProductEntity, isNew: Boolean = false) {
        viewModelScope.launch {
            repository.saveProduct(product, isNew)
            _adminMessage.value = if (isNew) "Product created in catalog" else "Product details updated"
            refreshAnalytics()
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            _adminMessage.value = "Product removed from catalog"
            refreshAnalytics()
        }
    }

    fun createCoupon(code: String, type: String, value: Double, minOrder: Double, maxDiscount: Double) {
        viewModelScope.launch {
            val coupon = CouponEntity(
                code = code.trim().uppercase(),
                type = type,
                value = value,
                minOrder = minOrder,
                maxDiscount = maxDiscount
            )
            repository.saveCoupon(coupon)
            _adminMessage.value = "Coupon '$code' created successfully"
        }
    }

    fun createCategory(name: String, slug: String, iconName: String) {
        viewModelScope.launch {
            val cat = CategoryEntity(
                name = name,
                slug = slug,
                iconName = iconName
            )
            repository.saveCategory(cat)
            _adminMessage.value = "Category '$name' added"
        }
    }

    class Factory(private val repository: NovexaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminViewModel(repository) as T
        }
    }
}
