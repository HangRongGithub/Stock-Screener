package com.example.stockscreener

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StockAdapter(
    private val stocks: List<Stock>,
    private val onStarClick: (Stock) -> Unit
) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    class StockViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val symbol: TextView = view.findViewById(R.id.symbol)
        val name: TextView = view.findViewById(R.id.name)
        val price: TextView = view.findViewById(R.id.price)
        val change: TextView = view.findViewById(R.id.change)
        val starButton: ImageView = view.findViewById(R.id.star_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = stocks[position]

        holder.symbol.text = stock.symbol
        holder.name.text = stock.name
        holder.price.text = "${stock.stock_price.current_price.amount} ${stock.stock_price.current_price.currency}"

        val changeText = "${stock.stock_price.price_change} (${stock.stock_price.percentage_change}%)"
        holder.change.text = changeText

        val color = if (stock.stock_price.price_change >= 0) {
            Color.parseColor("#4CAF50") // Green
        } else {
            Color.parseColor("#F44336") // Red
        }
        holder.change.setTextColor(color)

        // Set star button state
        holder.starButton.setImageResource(
            if (stock.isInWatchlist) R.drawable.ic_star_filled else R.drawable.ic_star_outline
        )

        // Handle star button click
        holder.starButton.setOnClickListener {
            onStarClick(stock)
        }
    }

    override fun getItemCount() = stocks.size
}