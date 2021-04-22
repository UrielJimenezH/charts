package com.leu8.charts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterRecyclerViewLegends(private val legends: ArrayList<Legend>): RecyclerView.Adapter<LegendViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_legend, parent, false)
        return LegendViewHolder(view)
    }

    override fun onBindViewHolder(holder: LegendViewHolder, position: Int) {
        holder.bind(legends[position])
    }

    override fun getItemCount(): Int = legends.count()
}

class LegendViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val ivColor: ImageView = view.findViewById(R.id.item_legend_iv_color)
    private val tvText: TextView = view.findViewById(R.id.item_legend_tv_text)

    fun bind(legend: Legend) {
        ivColor.setColorFilter(legend.color)
        tvText.text = legend.text
    }
}