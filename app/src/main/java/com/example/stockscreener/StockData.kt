package com.example.stockscreener

data class Stock(
    val id: Int,
    val symbol: String,
    val name: String,
    val logoUrl: String,
    val stock_price: StockPrice,
    var isInWatchlist: Boolean = false
)

data class StockPrice(
    val current_price: CurrentPrice,
    val price_change: Double,
    val percentage_change: Double
)

data class CurrentPrice(
    val amount: String,
    val currency: String
)

data class StockResponse(
    val stocks: List<Stock>
)