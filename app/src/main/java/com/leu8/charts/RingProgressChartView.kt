package com.leu8.charts

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat


class RingProgressChartView(context: Context, attrs: AttributeSet): View(context, attrs) {
    private val path: Path = Path()
    private val chartPaint: Paint = Paint()
    private val textPaint = Paint()
    private val oval: RectF = RectF()

    private var canvas: Canvas? = null

    var progressPercentage: Int = 50
        set(value) {
            field = value
            draw(canvas ?: return)
        }
    var barColor: Int = 0
    var barBackgroundColor: Int = 0
    var strokeWidth: Float = 0f
    var strokeRoundedCap: Boolean = true
    var text: String = ""
        set(value) {
            field = value
            draw(canvas ?: return)
        }
    var textSize: Float = 30f
    var textColor: Int = 0

    init {
        val attributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RingProgressChartView)

        val barColorId = attributes.getResourceId(R.styleable.RingProgressChartView_ringBarColor, 0)
        barColor = if (barColorId == 0) {
            val barColorString = attributes.getString(R.styleable.RingProgressChartView_ringBarColor)
            if (barColorString.isNullOrEmpty()) {
                ContextCompat.getColor(context, R.color.colorPrimary)
            } else {
                Color.parseColor(barColorString)
            }
        } else {
            ContextCompat.getColor(context, barColorId)
        }

        val barBackgroundColorId = attributes.getResourceId(R.styleable.RingProgressChartView_ringBackgroundColor, 0)
        barBackgroundColor = if (barBackgroundColorId == 0) {
            val barBackgroundColorString = attributes.getString(R.styleable.RingProgressChartView_ringBackgroundColor)
            if (barBackgroundColorString.isNullOrEmpty()) {
                ContextCompat.getColor(context, R.color.colorLightGray)
            } else {
                Color.parseColor(barBackgroundColorString)
            }
        } else {
            ContextCompat.getColor(context, barBackgroundColorId)
        }

        progressPercentage = attributes.getInteger(R.styleable.RingProgressChartView_ringProgressPercentage, 50)
        if (progressPercentage > 100)
            progressPercentage = 100

        strokeWidth = attributes.getDimension(R.styleable.RingProgressChartView_ringStrokeWidth, 30f)
        if (strokeWidth < 3)
            strokeWidth = 3f

        strokeRoundedCap = attributes.getBoolean(R.styleable.RingProgressChartView_ringStrokeRoundedCap, true)

        text = attributes.getString(R.styleable.RingProgressChartView_ringText)?: ""

        textSize = attributes.getDimension(R.styleable.RingProgressChartView_ringTextSize, 30f)
        if (textSize < 5)
            textSize = 5f

        val textColorId = attributes.getResourceId(R.styleable.RingProgressChartView_ringTextColor, 0)
        textColor = if (textColorId == 0) {
            val textColorString = attributes.getString(R.styleable.RingProgressChartView_ringTextColor)
            if (textColorString.isNullOrEmpty()) {
                ContextCompat.getColor(context, R.color.colorPrimary)
            } else {
                Color.parseColor(textColorString)
            }
        } else {
            ContextCompat.getColor(context, textColorId)
        }

        attributes.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (this.canvas == null)
            this.canvas = canvas

        val width = width.toFloat()
        val height = height.toFloat()

        val radius: Float
        radius = if (width > height) {
            height / 2f - strokeWidth / 2
        } else {
            width / 2f - strokeWidth / 2
        }

        path.addCircle(
            width / 2,
            height / 2, radius,
            Path.Direction.CW
        )

        chartPaint.color = barBackgroundColor
        chartPaint.strokeWidth = strokeWidth
        chartPaint.style = Paint.Style.STROKE
        chartPaint.isAntiAlias = true

        if (strokeRoundedCap)
            chartPaint.strokeCap = Paint.Cap.ROUND

        val centerX: Float
        val centerY: Float
        centerX = width / 2
        centerY = height / 2

        oval[centerX - radius, centerY - radius, centerX + radius] = centerY + radius

        canvas.drawArc(oval, 0f, 360f, false, chartPaint)

        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = textColor
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.isAntiAlias = true
        canvas.drawText(text, centerX, centerY + height / 15, textPaint)

        chartPaint.color = barColor

        val angle = (this.progressPercentage * 360 / 100).toFloat()
        canvas.drawArc(oval, 270f, -angle, false, chartPaint)
    }
}
