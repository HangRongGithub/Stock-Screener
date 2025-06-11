package com.example.stockscreener

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WatchlistFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var stockAdapter: StockAdapter
    private val watchlistStocks = mutableListOf<Stock>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_watchlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize WatchlistManager
        WatchlistManager.initialize(requireContext())

        recyclerView = view.findViewById(R.id.recycler_view)
        emptyStateText = view.findViewById(R.id.empty_state_text)

        recyclerView.layoutManager = LinearLayoutManager(context)

        stockAdapter = StockAdapter(watchlistStocks) { stock ->
            handleStarClick(stock)
        }
        recyclerView.adapter = stockAdapter

        loadWatchlist()
    }

    private fun loadWatchlist() {
        watchlistStocks.clear()
        watchlistStocks.addAll(WatchlistManager.getWatchlist())

        // Update UI based on watchlist content
        if (watchlistStocks.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateText.visibility = View.GONE
        }

        stockAdapter.notifyDataSetChanged()
    }

    private fun handleStarClick(stock: Stock) {
        WatchlistManager.removeFromWatchlist(stock)
        stock.isInWatchlist = false

        // Remove from local list and update adapter
        val position = watchlistStocks.indexOf(stock)
        if (position != -1) {
            watchlistStocks.removeAt(position)
            stockAdapter.notifyItemRemoved(position)
        }

        // Update UI if list becomes empty
        if (watchlistStocks.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateText.visibility = View.VISIBLE
        }

        Toast.makeText(requireContext(), "${stock.symbol} removed from watchlist", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh watchlist when returning to fragment
        loadWatchlist()
    }
}