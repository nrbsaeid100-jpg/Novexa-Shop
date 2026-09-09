package com.example.novexa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.novexa.data.local.entity.*
import com.example.novexa.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ProductSortOption(val displayName: String) {
    POPULAR("Popular"),
    NEWEST("Newest"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    HIGHEST_RATED("Highest Rated"),
    BIGGEST_DISCOUNT("Biggest Discount")
}

data class FilterState(
    val searchQuery: String = "",
    val categoryId: Long? = null,
    val brand: String? = null,
    val minPrice: Double = 0.0,
    val maxPrice: Double = 30000.0,
    val minRating: Double = 0.0,
    val inStockOnly: Boolean = false,
    val sortOption: ProductSortOption = ProductSortOption.POPULAR
)

class NovexaViewModel(
    private val repository: NovexaRepository
) : ViewModel() {

    // --- User / Auth State ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // --- Product Catalog State ---
    val allProducts: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredProducts: StateFlow<List<ProductEntity>> = repository.getFeaturedProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashSaleProducts: StateFlow<List<ProductEntity>> = repository.getFlashSaleProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bestsellers: StateFlow<List<ProductEntity>> = repository.getBestsellers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Search & Filtering ---
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        _filterState
    ) { products, filter ->
        var list = products

        if (filter.searchQuery.isNotBlank()) {
            val q = filter.searchQuery.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.tags.lowercase().contains(q) ||
                it.sku.lowercase().contains(q)
            }
        }

        if (filter.categoryId != null) {
            list = list.filter { it.categoryId == filter.categoryId || it.subcategoryId == filter.categoryId }
        }

        if (filter.brand != null && filter.brand.isNotBlank()) {
            list = list.filter { it.brand.equals(filter.brand, ignoreCase = true) }
        }

        list = list.filter { it.price in filter.minPrice..filter.maxPrice }

        if (filter.minRating > 0) {
            list = list.filter { it.rating >= filter.minRating }
        }

        if (filter.inStockOnly) {
            list = list.filter { it.stockQuantity > 0 }
        }

        when (filter.sortOption) {
            ProductSortOption.POPULAR -> list.sortedByDescending { it.reviewCount }
            ProductSortOption.NEWEST -> list.sortedByDescending { it.createdAt }
            ProductSortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
            ProductSortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
            ProductSortOption.HIGHEST_RATED -> list.sortedByDescending { it.rating }
            ProductSortOption.BIGGEST_DISCOUNT -> list.sortedByDescending { it.discountPercent }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Cart & Checkout ---
    private val currentUserId: Long
        get() = _currentUser.value?.id ?: 2L // default to seeded demo customer or 0

    val cartItems: StateFlow<List<CartItemWithProduct>> = _currentUser.flatMapLatest { user ->
        repository.getCartWithProducts(user?.id ?: 2L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _appliedCouponCode = MutableStateFlow<String?>(null)
    val appliedCouponCode: StateFlow<String?> = _appliedCouponCode.asStateFlow()

    private val _isInsideCity = MutableStateFlow(true) // true = Inside Dhaka (৳60), false = Outside Dhaka (৳120)
    val isInsideCity: StateFlow<Boolean> = _isInsideCity.asStateFlow()

    private val _cartPricing = MutableStateFlow(CartPricingResult(0.0, 0.0, 60.0, 60.0))
    val cartPricing: StateFlow<CartPricingResult> = _cartPricing.asStateFlow()

    // --- Wishlist ---
    val wishlist: StateFlow<List<ProductEntity>> = _currentUser.flatMapLatest { user ->
        repository.getWishlist(user?.id ?: 2L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Orders ---
    val userOrders: StateFlow<List<OrderEntity>> = _currentUser.flatMapLatest { user ->
        repository.getOrdersForUser(user?.id ?: 2L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Notifications ---
    val notifications: StateFlow<List<NotificationEntity>> = _currentUser.flatMapLatest { user ->
        repository.getNotifications(user?.id ?: 2L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Addresses ---
    val addresses: StateFlow<List<AddressEntity>> = _currentUser.flatMapLatest { user ->
        repository.getAddresses(user?.id ?: 2L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback Message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Last placed order for tracking screen
    private val _latestOrder = MutableStateFlow<OrderEntity?>(null)
    val latestOrder: StateFlow<OrderEntity?> = _latestOrder.asStateFlow()

    init {
        // Automatically calculate pricing whenever cart items, coupon, or delivery location changes
        viewModelScope.launch {
            combine(cartItems, _appliedCouponCode, _isInsideCity) { items, coupon, insideCity ->
                repository.calculateCartTotals(items, coupon, insideCity)
            }.collect { pricing ->
                _cartPricing.value = pricing
            }
        }

        // Initialize with demo customer
        viewModelScope.launch {
            val customer = repository.getUser(2L)
            if (customer != null) {
                _currentUser.value = customer
            }
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // --- Auth Actions ---
    fun login(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                _authError.value = null
                _userMessage.value = "Welcome back, ${user.name}!"
                onSuccess()
            }.onFailure { err ->
                _authError.value = err.message
            }
        }
    }

    fun register(name: String, email: String, phone: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.register(name, email, phone, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                _authError.value = null
                _userMessage.value = "Registration successful! Welcome to Novexa."
                onSuccess()
            }.onFailure { err ->
                _authError.value = err.message
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _userMessage.value = "Logged out successfully"
    }

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
        _userMessage.value = "Switched to ${user.name} (${user.role})"
    }

    // --- Search & Filter Actions ---
    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun selectCategory(categoryId: Long?) {
        _filterState.value = _filterState.value.copy(categoryId = categoryId)
    }

    fun setBrandFilter(brand: String?) {
        _filterState.value = _filterState.value.copy(brand = brand)
    }

    fun setPriceRange(min: Double, max: Double) {
        _filterState.value = _filterState.value.copy(minPrice = min, maxPrice = max)
    }

    fun setSortOption(sort: ProductSortOption) {
        _filterState.value = _filterState.value.copy(sortOption = sort)
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    // --- Cart Actions ---
    fun addToCart(productId: Long, variantId: Long? = null, quantity: Int = 1) {
        viewModelScope.launch {
            val result = repository.addToCart(currentUserId, productId, variantId, quantity)
            result.onSuccess {
                _userMessage.value = "Item added to cart!"
            }.onFailure { err ->
                _userMessage.value = err.message ?: "Failed to add to cart"
            }
        }
    }

    fun updateCartQuantity(cartItemId: Long, newQty: Int, maxStock: Int) {
        viewModelScope.launch {
            val success = repository.updateCartQuantity(cartItemId, newQty, maxStock)
            if (!success && newQty > maxStock) {
                _userMessage.value = "Cannot exceed available stock of $maxStock"
            }
        }
    }

    fun removeFromCart(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(cartItemId)
        }
    }

    fun applyCoupon(code: String) {
        _appliedCouponCode.value = code.trim().uppercase()
    }

    fun removeCoupon() {
        _appliedCouponCode.value = null
    }

    fun setShippingLocation(insideCity: Boolean) {
        _isInsideCity.value = insideCity
    }

    // --- Wishlist Actions ---
    fun toggleWishlist(productId: Long) {
        viewModelScope.launch {
            val added = repository.toggleWishlist(currentUserId, productId)
            _userMessage.value = if (added) "Saved to wishlist" else "Removed from wishlist"
        }
    }

    // --- Checkout & Order Placement ---
    fun placeOrder(
        fullName: String,
        phone: String,
        email: String,
        division: String,
        district: String,
        upazila: String,
        fullAddress: String,
        deliveryInstructions: String,
        paymentMethod: String,
        paymentPin: String,
        onSuccess: (OrderEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val request = CheckoutRequest(
                userId = currentUserId,
                fullName = fullName,
                phone = phone,
                email = email,
                division = division,
                district = district,
                upazila = upazila,
                fullAddress = fullAddress,
                deliveryInstructions = deliveryInstructions,
                paymentMethod = paymentMethod,
                paymentPinOrCode = paymentPin,
                couponCode = _appliedCouponCode.value,
                isInsideCity = _isInsideCity.value
            )

            val result = repository.placeOrder(request)
            result.onSuccess { order ->
                _latestOrder.value = order
                _appliedCouponCode.value = null
                _userMessage.value = "Order placed successfully! Order #${order.orderNumber}"
                onSuccess(order)
            }.onFailure { err ->
                onError(err.message ?: "Failed to place order")
            }
        }
    }

    fun cancelOrder(orderId: Long, reason: String) {
        viewModelScope.launch {
            val result = repository.cancelOrder(orderId, reason)
            result.onSuccess {
                _userMessage.value = "Order cancelled and stock restored."
            }.onFailure { err ->
                _userMessage.value = err.message ?: "Failed to cancel order"
            }
        }
    }

    // --- Reviews ---
    fun submitReview(productId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            val userName = user?.name ?: "Verified Customer"
            val userId = user?.id ?: 2L
            repository.submitReview(productId, userId, userName, rating, comment)
            _userMessage.value = "Thank you! Your review has been published."
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun saveAddress(address: AddressEntity) {
        viewModelScope.launch {
            repository.saveAddress(address.copy(userId = currentUserId))
            _userMessage.value = "Delivery address saved"
        }
    }

    class Factory(private val repository: NovexaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NovexaViewModel(repository) as T
        }
    }
}
