package com.leu8.charts

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

class PieChartView @JvmOverloads constructor(context: Context,
                                             attributeSet: AttributeSet? = null,
                                             defStyleAttr: Int = 0) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    private lateinit var tvTitle: TextView
    private lateinit var chart: PieChart
    private lateinit var rvLegends: RecyclerView

    private val colors = ArrayList<Int>()
    private val colorsStr = arrayOf(
        "#82ab80", "#bfd3ed", "#fea7a2", "#c490dd", "#fce2bb", "#ace280"
    )

    var valueFormatter: ValueFormatter =
        object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

    var textColor: Int = 0
    var chartTitle: String = ""
        set(value) {
            field = value
            tvTitle.text = value

            tvTitle.visibility = if (value.isEmpty()) View.GONE else View.VISIBLE
        }
    var legendsNumColumns: Int = 4
    var usePercentValues = false
        set(value) {
            //TODO UPDATE CHART
        }

    init {
        val attributes: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PieChartView)

        val chartTitleId = attributes.getResourceId(R.styleable.PieChartView_pieChartTitle, 0)
        legendsNumColumns = attributes.getInt(R.styleable.PieChartView_pieLegendsNumColumns, legendsNumColumns)
        val textColorId = attributes.getResourceId(R.styleable.PieChartView_pieTextColor, 0)
        textColor = if (textColorId == 0) {
            val mainColorString = attributes.getString(R.styleable.PieChartView_pieTextColor)
            if (mainColorString.isNullOrEmpty())
                ContextCompat.getColor(context, R.color.colorPrimary)
            else
                Color.parseColor(mainColorString)
        } else
            ContextCompat.getColor(context, textColor)

       
        attributes.getResourceId(R.styleable.PieChartView_pieChartTitle, 0)
        
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.pie_chart_view, this)
        attributes.recycle()

        bindViews()

        tvTitle.setTextColor(textColor)
        chartTitle = if (chartTitleId != 0)
            context.getString(chartTitleId)
        else ""

        chart.description.isEnabled = false
        chart.dragDecelerationFrictionCoef = 0.95f
        chart.setExtraOffsets(20f, 20f, 20f, 20f)
        chart.isDrawHoleEnabled = false
        chart.rotationAngle = 0f
        chart.isRotationEnabled = false
        chart.setDrawEntryLabels(false)

        chart.animateY(1400, Easing.EaseInOutQuad)
        chart.legend.isEnabled = false
    }

    private fun bindViews() {
        tvTitle = findViewById(R.id.pie_polyline_chart_view_tv_chart_title)
        chart = findViewById(R.id.pie_polyline_chart_view_chart)
        rvLegends = findViewById(R.id.pie_polyline_chart_view_rv_legends)
    }

    fun setData(entries: ArrayList<PieChartEntry>) {
        val legends = ArrayList<Legend>()
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        val pieChartEntries = entries.map { entry ->
            PieEntry(entry.value, entry.label)
        }

        for (i in 0 until entries.size) {
            colors.add(Color.parseColor(colorsStr[i % colorsStr.size]))
            legends.add(Legend(entries[i].label, colors[i]))
        }

        rvLegends.adapter = AdapterRecyclerViewLegends(legends)
        rvLegends.layoutManager = GridLayoutManager(context, 4)

        val dataSet = PieDataSet(pieChartEntries, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 0f

        dataSet.colors = colors
//        dataSet.valueLinePart1OffsetPercentage = 80f
//        dataSet.valueLinePart1Length = 0.4f
//        dataSet.valueLinePart2Length = 0.8f
        dataSet.valueLineColor = ContextCompat.getColor(context, R.color.colorTransparent)

//        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
//        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val data = PieData(dataSet)
        data.setValueFormatter(valueFormatter)
        data.setValueTextSize(10f)
        data.setValueTextColor(ContextCompat.getColor(context, R.color.colorLightGray))
        chart.data = data

        chart.highlightValues(null)
        chart.invalidate()
    }
}
