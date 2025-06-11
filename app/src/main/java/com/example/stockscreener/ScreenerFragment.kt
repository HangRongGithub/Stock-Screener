package com.example.stockscreener

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson

class ScreenerFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: TextInputEditText
    private lateinit var filterSpinner: Spinner
    private lateinit var noResultsText: TextView
    private lateinit var stockAdapter: StockAdapter

    private val allStocks = mutableListOf<Stock>()
    private val filteredStocks = mutableListOf<Stock>()

    private var currentSearchQuery = ""
    private var currentFilter = FilterType.ALL

    enum class FilterType(val displayName: String) {
        ALL("All"),
        POSITIVE("Increasing (+)"),
        NEGATIVE("Decreasing (-)")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_screener, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize WatchlistManager
        WatchlistManager.initialize(requireContext())

        initializeViews(view)
        setupRecyclerView()
        setupSearchBar()
        setupFilterSpinner()
        loadStockData()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        searchEditText = view.findViewById(R.id.search_edit_text)
        filterSpinner = view.findViewById(R.id.filter_spinner)
        noResultsText = view.findViewById(R.id.no_results_text)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        stockAdapter = StockAdapter(filteredStocks) { stock ->
            handleStarClick(stock)
        }
        recyclerView.adapter = stockAdapter
    }

    private fun setupSearchBar() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                currentSearchQuery = s.toString().trim()
                applyFilters()
            }
        })
    }

    private fun setupFilterSpinner() {
        val filterOptions = FilterType.values().map { it.displayName }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            filterOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = adapter

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentFilter = FilterType.values()[position]
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadStockData() {
        val jsonString = loadJSONFromAssets()
        val stockResponse = parseStockData(jsonString)

        allStocks.clear()
        allStocks.addAll(stockResponse.stocks)

        // Update watchlist status for each stock
        updateWatchlistStatus()

        // Apply initial filters (show all)
        applyFilters()
    }

    private fun applyFilters() {
        filteredStocks.clear()

        var filtered = allStocks.toList()

        // Apply search filter
        if (currentSearchQuery.isNotEmpty()) {
            filtered = filtered.filter { stock ->
                stock.name.contains(currentSearchQuery, ignoreCase = true) ||
                        stock.symbol.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        // Apply price change filter
        filtered = when (currentFilter) {
            FilterType.ALL -> filtered
            FilterType.POSITIVE -> filtered.filter { it.stock_price.price_change >= 0 }
            FilterType.NEGATIVE -> filtered.filter { it.stock_price.price_change < 0 }
        }

        filteredStocks.addAll(filtered)

        // Update UI
        updateUI()
    }

    private fun updateUI() {
        stockAdapter.notifyDataSetChanged()

        if (filteredStocks.isEmpty()) {
            recyclerView.visibility = View.GONE
            noResultsText.visibility = View.VISIBLE

            // Set appropriate message based on current filters
            noResultsText.text = when {
                currentSearchQuery.isNotEmpty() && currentFilter != FilterType.ALL ->
                    "No ${currentFilter.displayName.lowercase()} stocks found for \"$currentSearchQuery\""
                currentSearchQuery.isNotEmpty() ->
                    "No stocks found for \"$currentSearchQuery\""
                currentFilter != FilterType.ALL ->
                    "No ${currentFilter.displayName.lowercase()} stocks found"
                else -> "No stocks available"
            }
        } else {
            recyclerView.visibility = View.VISIBLE
            noResultsText.visibility = View.GONE
        }
    }

    private fun loadJSONFromAssets(): String {
        return requireContext().assets.open("stocks.json")
            .bufferedReader().use { it.readText() }
    }

    private fun parseStockData(jsonString: String): StockResponse {
        return Gson().fromJson(jsonString, StockResponse::class.java)
    }

    private fun updateWatchlistStatus() {
        allStocks.forEach { stock ->
            stock.isInWatchlist = WatchlistManager.isInWatchlist(stock.id)
        }
    }

    private fun handleStarClick(stock: Stock) {
        val isNowInWatchlist = WatchlistManager.toggleWatchlist(stock)
        stock.isInWatchlist = isNowInWatchlist

        // Update the adapter
        val position = filteredStocks.indexOf(stock)
        if (position != -1) {
            stockAdapter.notifyItemChanged(position)
        }

        // Show toast feedback
        val message = if (isNowInWatchlist) {
            "${stock.symbol} added to watchlist"
        } else {
            "${stock.symbol} removed from watchlist"
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh watchlist status when returning to fragment
        updateWatchlistStatus()
        applyFilters() // Re-apply filters to refresh the display
    }
}