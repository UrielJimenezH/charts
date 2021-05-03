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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.text.DecimalFormat
import kotlin.math.min

class BarChartView @JvmOverloads constructor(ctx: Context,
                                                    attributeSet: AttributeSet? = null,
                                                    defStyleAttr: Int = 0) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {
    private var tvChartTitle: TextView
    private var tvChartDescription: TextView
    private var barChart: BarChart
    private var tvLeftAmount: TextView
    private var tvLeftAmountDescription: TextView
    private var tvRightAmount: TextView
    private var tvRightAmountDescription: TextView

    var leftAxisTextSize = 12f
    var xAxisTextSize = 10f
    var legendsTextSize = 10f
    var legendsFormSize = 15f
    var mainColor: Int = 0
    var chartTitle: String = ""
        set(value) {
            field = value
    //        tvChartTitle.text = value

    //        tvChartTitle.visibility = if (value.isEmpty()) View.GONE else View.VISIBLE
            reDrawView()
        }
    var chartDescription: String = ""
        set(value) {
            field = value
            reDrawView()
        }


    private val df: DecimalFormat = DecimalFormat("#,##0")

    var leftAmount: Int? = null
        set(value) {
            field = value
            tvLeftAmount.text = "$ ${df.format(value)} MXN"

//            if (value == null) {
//                tvLeftAmount.visibility =  View.GONE
//                tvLeftAmountDescription.visibility =  View.GONE
//            } else
//                tvLeftAmount.visibility =  View.VISIBLE
            reDrawView()
        }
    var leftAmountDescription: String = ""
        set(value) {
            field = value
            tvLeftAmountDescription.text = value
//
//            tvLeftAmountDescription.visibility = if (value.isEmpty()) View.GONE else View.VISIBLE
            reDrawView()
        }


    var rightAmount: Int? = null
        set(value) {
            field = value
            tvRightAmount.text = "$ ${df.format(value)} MXN"

//            if (value == null) {
//                tvRightAmount.visibility =  View.GONE
//                tvRightAmountDescription.visibility =  View.GONE
//            } else
//                tvRightAmount.visibility =  View.VISIBLE
            reDrawView()
        }
    var rightAmountDescription: String = ""
        set(value) {
            field = value
            tvRightAmountDescription.text = value

//            tvRightAmountDescription.visibility = if (value.isEmpty()) View.GONE else View.VISIBLE
            reDrawView()
        }

    private var xAxisLabels: ArrayList<String> = ArrayList()
    private var chartValues = ArrayList<BarEntry>()

    private var colors: IntArray = IntArray(10)

    private val leftAxisValueFormatter: ValueFormatter =
        object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

    private val xAxisValueFormatter: ValueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return xAxisLabels[value.toInt()]
        }
    }

    init {
        val attributes: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.BarChartView)

        val chartTitleId = attributes.getResourceId(R.styleable.BarChartView_barChartTitle, 0)
        val chartDescriptionId = attributes.getResourceId(R.styleable.BarChartView_barChartDescription, 0)
        val leftAmountDescriptionId = attributes.getResourceId(R.styleable.BarChartView_barLeftAmountDescription, 0)
        val rightAmountDescriptionId = attributes.getResourceId(R.styleable.BarChartView_barRightAmountDescription, 0)
        val mainColorId = attributes.getResourceId(R.styleable.BarChartView_barMainColor, 0)
        mainColor = if (mainColorId == 0) {
            val mainColorString = attributes.getString(R.styleable.BarChartView_barMainColor)
            if (mainColorString.isNullOrEmpty())
                ContextCompat.getColor(context, R.color.colorPrimary)
            else
                Color.parseColor(mainColorString)
        } else
            ContextCompat.getColor(context, mainColorId)


        leftAxisTextSize =
            min(attributes.getDimension(R.styleable.BarChartView_barLeftAxisTextSize, leftAxisTextSize),
                leftAxisTextSize)

        xAxisTextSize =
            min(attributes.getDimension(R.styleable.BarChartView_barXAxisTextSize, xAxisTextSize),
                xAxisTextSize)

        legendsTextSize =
            min(attributes.getDimension(R.styleable.BarChartView_barLegendsTextSize, legendsTextSize),
                legendsTextSize)

        legendsFormSize =
            min(attributes.getDimension(R.styleable.BarChartView_barLegendFormSize, legendsFormSize),
                legendsFormSize)

        attributes.recycle()

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.bar_chart_view, this)

        tvChartTitle = findViewById(R.id.bar_chart_view_tv_chart_title)
        tvChartDescription = findViewById(R.id.bar_chart_view_tv_chart_description)
        barChart = findViewById(R.id.bar_chart_view_chart)
        tvLeftAmount = findViewById(R.id.bar_chart_view_tv_left_amount)
        tvLeftAmountDescription = findViewById(R.id.bar_chart_view_tv_left_amount_description)
        tvRightAmount = findViewById(R.id.bar_chart_view_tv_right_amount)
        tvRightAmountDescription = findViewById(R.id.bar_chart_view_tv_right_amount_description)

        tvChartTitle.setTextColor(mainColor)
        tvChartTitle.text = if (chartTitleId != 0)
            ctx.getString(chartTitleId)
        else chartTitle

        tvChartDescription.setTextColor(mainColor)
        tvChartDescription.text = if (chartDescriptionId != 0)
            ctx.getString(chartDescriptionId)
        else chartDescription
//        tvChartDescription.visibility = chartDescriptionVisibility

        tvLeftAmountDescription.setTextColor(mainColor)
        tvLeftAmountDescription.text = if (leftAmountDescriptionId != 0)
            ctx.getString(leftAmountDescriptionId)
        else leftAmountDescription

        tvRightAmountDescription.setTextColor(mainColor)
        tvRightAmountDescription.text = if (rightAmountDescriptionId != 0)
            ctx.getString(rightAmountDescriptionId)
        else rightAmountDescription

        setupChartView()
    }

    private fun setupChartView() {
        barChart.description.isEnabled = false
        barChart.setMaxVisibleValueCount(40)
        barChart.setScaleEnabled(false)
        barChart.setPinchZoom(false)
        barChart.isAutoScaleMinMaxEnabled = false
        barChart.isDoubleTapToZoomEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setFitBars(true)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.yOffset = 10f
        xAxis.labelRotationAngle = 0f
        xAxis.textColor = mainColor
        xAxis.valueFormatter = xAxisValueFormatter
        xAxis.setDrawGridLines(false)
        xAxis.gridColor = mainColor
        xAxis.textColor = mainColor
        xAxis.axisLineColor = mainColor
        xAxis.textSize = xAxisTextSize
        xAxis.spaceMax = 0f

        val rightAxis = barChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.zeroLineColor = mainColor
        rightAxis.setDrawZeroLine(false)
        rightAxis.zeroLineWidth = 0f
        rightAxis.axisLineColor = mainColor
        rightAxis.gridColor = mainColor
        rightAxis.textColor = mainColor
        rightAxis.isEnabled = false

        val leftAxis = barChart.axisLeft
        leftAxis.zeroLineColor = mainColor
        leftAxis.setDrawZeroLine(false)
        leftAxis.zeroLineWidth = 0f
        leftAxis.axisLineColor = ContextCompat.getColor(context, R.color.colorTransparent)
        leftAxis.gridColor = mainColor
        leftAxis.textColor = mainColor
        leftAxis.textSize = leftAxisTextSize
        leftAxis.valueFormatter = leftAxisValueFormatter
        leftAxis.axisMinimum = 0f

        barChart.legend.isEnabled = false
    }

    fun setData(chartValues: ArrayList<Float>, xAxisLabels: ArrayList<String>, colors: IntArray) {
        if (chartValues.size != xAxisLabels.size)
            return

        this.xAxisLabels = xAxisLabels
        this.colors = colors

        chartValues.mapIndexedTo(this.chartValues) { index, arrayList ->
            BarEntry(index.toFloat(), arrayList)
        }

        val valuesSet: BarDataSet
        if (barChart.data != null && barChart.data.dataSetCount > 0) {
            valuesSet = barChart.data.getDataSetByIndex(0) as BarDataSet
            valuesSet.values = this.chartValues
            barChart.data.notifyDataChanged()
            barChart.notifyDataSetChanged()
        } else {
            valuesSet = BarDataSet(this.chartValues, "")
            valuesSet.setDrawIcons(false)

            valuesSet.setColors(*this.colors)
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(valuesSet)
            val data = BarData(dataSets)
            data.setDrawValues(false)
            barChart.data = data
        }

        barChart.data.isHighlightEnabled = false
        barChart.invalidate()
    }

    private fun reDrawView() {
        invalidate()
        requestLayout()
    }
}