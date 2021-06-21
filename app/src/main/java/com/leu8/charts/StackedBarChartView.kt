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
import kotlin.math.min
import kotlin.math.roundToInt

class StackedBarChartView @JvmOverloads constructor(ctx: Context,
                                                    attributeSet: AttributeSet? = null,
                                                    defStyleAttr: Int = 0) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {
    private var tvChartDescription: TextView
    private var stackedBarChart: BarChart

    var leftAxisTextSize = 12f
    var xAxisTextSize = 10f
    var legendsTextSize = 10f
    var legendsFormSize = 15f
    var mainColor: Int = 0
    var textDescription: String = ""
        set(value) {
            field = value
            tvChartDescription.text = value

            tvChartDescription.visibility = if (value.isNullOrEmpty()) View.GONE else View.VISIBLE
            reDrawView()
        }

    private var xAxisLabels: ArrayList<String> = ArrayList()
    private var chartValues = ArrayList<BarEntry>()

    private var stackColors: IntArray = IntArray(10)

    private val leftAxisValueFormatter: ValueFormatter =
        object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.roundToInt().toString()
            }
        }

    private val xAxisValueFormatter: ValueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return xAxisLabels[value.toInt()]
        }
    }

    init {
        val attributes: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.StackedBarChartView)

        val chartDescriptionId = attributes.getResourceId(R.styleable.StackedBarChartView_stackedBarChartDescription, 0)
        val mainColorId = attributes.getResourceId(R.styleable.StackedBarChartView_stackedBarMainColor, 0)
        mainColor = if (mainColorId == 0) {
            val mainColorString = attributes.getString(R.styleable.StackedBarChartView_stackedBarMainColor)
            if (mainColorString.isNullOrEmpty())
                ContextCompat.getColor(context, R.color.colorPrimary)
            else
                Color.parseColor(mainColorString)
        } else
            ContextCompat.getColor(context, mainColorId)


        leftAxisTextSize =
            min(attributes.getDimension(R.styleable.StackedBarChartView_stackedBarLeftAxisTextSize, leftAxisTextSize),
                leftAxisTextSize)

        xAxisTextSize =
            min(attributes.getDimension(R.styleable.StackedBarChartView_stackedBarXAxisTextSize, xAxisTextSize),
                xAxisTextSize)

        legendsTextSize =
            min(attributes.getDimension(R.styleable.StackedBarChartView_stackedBarLegendsTextSize, legendsTextSize),
                legendsTextSize)

        legendsFormSize =
            min(attributes.getDimension(R.styleable.StackedBarChartView_stackedBarLegendFormSize, legendsFormSize),
                legendsFormSize)

        attributes.recycle()

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.stacked_bar_chart_view, this)

        tvChartDescription = findViewById(R.id.stacked_bar_chart_view_tv_chart_description)
        stackedBarChart = findViewById(R.id.stacked_bar_chart_view_chart)

        tvChartDescription.setTextColor(mainColor)
        textDescription = if (chartDescriptionId != 0)
            ctx.getString(chartDescriptionId)
        else ""


        setupChartView()
    }

    private fun setupChartView() {
        stackedBarChart.description.isEnabled = false
        stackedBarChart.setMaxVisibleValueCount(40)
        stackedBarChart.setScaleEnabled(false)
        stackedBarChart.setPinchZoom(false)
        stackedBarChart.isAutoScaleMinMaxEnabled = false
        stackedBarChart.isDoubleTapToZoomEnabled = false
        stackedBarChart.setDrawGridBackground(false)
        stackedBarChart.setFitBars(true)

        val xAxis = stackedBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.yOffset = 10f
        xAxis.labelRotationAngle = 90f
        xAxis.textColor = mainColor
        xAxis.valueFormatter = xAxisValueFormatter
        xAxis.setDrawGridLines(false)
        xAxis.gridColor = mainColor
        xAxis.textColor = mainColor
        xAxis.axisLineColor = mainColor
        xAxis.textSize = xAxisTextSize

        val rightAxis = stackedBarChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.zeroLineColor = mainColor
        rightAxis.setDrawZeroLine(false)
        rightAxis.zeroLineWidth = 0f
        rightAxis.axisLineColor = mainColor
        rightAxis.gridColor = mainColor
        rightAxis.textColor = mainColor
        rightAxis.isEnabled = false

        val leftAxis = stackedBarChart.axisLeft
        leftAxis.zeroLineColor = mainColor
        leftAxis.setDrawZeroLine(false)
        leftAxis.zeroLineWidth = 0f
        leftAxis.axisLineColor = ContextCompat.getColor(context, R.color.colorTransparent)
        leftAxis.gridColor = mainColor
        leftAxis.textColor = mainColor
        leftAxis.textSize = leftAxisTextSize
        leftAxis.valueFormatter = leftAxisValueFormatter
        leftAxis.axisMinimum = 0f

        val legend = stackedBarChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.textColor =  mainColor
        legend.form = Legend.LegendForm.CIRCLE
        legend.formSize = legendsFormSize
        legend.formToTextSpace = 8f
        legend.xEntrySpace = 20f
        legend.textSize = legendsTextSize

        stackedBarChart.extraTopOffset = 10f
    }

    fun setData(chartValues: ArrayList<FloatArray>, xAxisLabels: ArrayList<String>, stackLabels: Array<String>, stackColors: IntArray) {
        if (chartValues.size != xAxisLabels.size)
            return

        this.xAxisLabels = xAxisLabels
        this.stackColors = stackColors

//        var anyValueDifferentFromZero = false
//        chartValues.forEach { valuesArray ->
//            valuesArray.forEach { value ->
//                if (value != 0f)
//                    anyValueDifferentFromZero = true
//            }
//        }

//        if (!anyValueDifferentFromZero) {
//            this.stackColors = intArrayOf(stackColors.for, ContextCompat.getColor(context, android.R.color.transparent))
            chartValues.add(floatArrayOf(0f, 0f, 10f))
//        }

        chartValues.mapIndexedTo(this.chartValues) { index, floatArray ->
            val newValuesArrayList = ArrayList<Float>()

            var sum = 0f
            floatArray.forEach { value ->
                newValuesArrayList.add(value - sum)
                sum += value
            }

            BarEntry(index.toFloat(), newValuesArrayList.toFloatArray())
        }

        val valuesSet: BarDataSet
        if (stackedBarChart.data != null && stackedBarChart.data.dataSetCount > 0) {
            valuesSet = stackedBarChart.data.getDataSetByIndex(0) as BarDataSet
            valuesSet.values = this.chartValues
            stackedBarChart.data.notifyDataChanged()
            stackedBarChart.notifyDataSetChanged()
        } else {
            valuesSet = BarDataSet(this.chartValues, "")
            valuesSet.setDrawIcons(false)

            valuesSet.setColors(*this.stackColors)
            valuesSet.stackLabels = stackLabels
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(valuesSet)
            val data = BarData(dataSets)
            data.setDrawValues(false)
            stackedBarChart.data = data
        }

        stackedBarChart.data.isHighlightEnabled = false
        stackedBarChart.invalidate()
    }

    private fun reDrawView() {
        invalidate()
        requestLayout()
    }
}