package com.example.stockscreener

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object WatchlistManager {
    private const val PREF_NAME = "watchlist_prefs"
    private const val WATCHLIST_KEY = "watchlist_stocks"

    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun addToWatchlist(stock: Stock) {
        val watchlist = getWatchlist().toMutableList()
        if (!watchlist.any { it.id == stock.id }) {
            watchlist.add(stock)
            saveWatchlist(watchlist)
        }
    }

    fun removeFromWatchlist(stock: Stock) {
        val watchlist = getWatchlist().toMutableList()
        watchlist.removeAll { it.id == stock.id }
        saveWatchlist(watchlist)
    }

    fun isInWatchlist(stockId: Int): Boolean {
        return getWatchlist().any { it.id == stockId }
    }

    fun getWatchlist(): List<Stock> {
        val json = sharedPreferences?.getString(WATCHLIST_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Stock>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveWatchlist(watchlist: List<Stock>) {
        val json = gson.toJson(watchlist)
        sharedPreferences?.edit()?.putString(WATCHLIST_KEY, json)?.apply()
    }

    fun toggleWatchlist(stock: Stock): Boolean {
        return if (isInWatchlist(stock.id)) {
            removeFromWatchlist(stock)
            false
        } else {
            addToWatchlist(stock)
            true
        }
    }
}