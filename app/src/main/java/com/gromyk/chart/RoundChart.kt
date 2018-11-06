package com.gromyk.chart

import android.annotation.SuppressLint

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * Created by Yuriy Gromyk on 11/5/18.
 */
@SuppressLint("LogNotTimber")
class RoundChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val START_ANGLE = -90
        private const val pickerRadius = 25f
        private const val EXTERNAL_CIRCLE_COEFFICIENT_RADIUS = 1.05f
        private const val INNER_CIRCLE_COEFFICIENT_RADIUS = 1.25f
        private const val MAIN_CIRCLE_COEFFICIENT_RADIUS = 2f

        @Throws(IllegalArgumentException::class)
        fun getAngleByPercent(percent: Float): Float {
            if (percent > 100f || percent < 0f)
                throw IllegalArgumentException("Value " + percent.toString() + " cannot be used as percent!")
            return (360f / 100f) * percent
        }

        fun createAntiAliasingPaint(): Paint {
            val paint = Paint()
            paint.flags = Paint.ANTI_ALIAS_FLAG
            paint.isAntiAlias = true
            return paint
        }
    }

    private var viewWidth = 0
    private var viewHeight = 0
    private var shadowRadius = 50f
    private var strokeWidth = 20f
    private var textSize = 30f

    var percentExternal: Float = 0f
    var percentInner: Float = 0f

    private lateinit var externalCirclePaint: Paint
    private lateinit var innerCirclePaint: Paint
    private lateinit var externalCirclePickerPaint: Paint
    private lateinit var innerCirclePickerPaint: Paint
    private lateinit var mainCirclePaint: Paint
    private lateinit var labelPaint: Paint
    private lateinit var restOfExternalCircle: Paint
    private lateinit var restOfInnerCircle: Paint

    private lateinit var gradientPicker: LinearGradient
    private lateinit var mainCircleGradient: LinearGradient
    private lateinit var secondaryCircleGradient: LinearGradient

    init {
        init(attrs)
        initPaints()
    }

    private fun initPaints() {
        externalCirclePaint = createAntiAliasingPaint()
        innerCirclePaint = createAntiAliasingPaint()
        externalCirclePickerPaint = createAntiAliasingPaint()
        innerCirclePickerPaint = createAntiAliasingPaint()
        mainCirclePaint = createAntiAliasingPaint()
        restOfExternalCircle = createAntiAliasingPaint()
        restOfInnerCircle = createAntiAliasingPaint()
        setupLabelPaint()
        setupCirclePaint(innerCirclePaint, Paint.Style.STROKE)
        setupCirclePaint(externalCirclePaint, Paint.Style.STROKE)
        setupRestOfCircle(restOfExternalCircle)
        setupRestOfCircle(restOfInnerCircle)
        setupMainCircle(mainCirclePaint)
    }


    private fun setupCirclePaint(paint: Paint, style: Paint.Style) {
        paint.isAntiAlias = true
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.color = Color.CYAN
        paint.style = style
        paint.strokeWidth = strokeWidth
    }

    private fun setPaintGradient(paint: Paint, linearGradient: LinearGradient) {
        paint.setShadowLayer(shadowRadius, 0f, 0f, Color.BLACK)
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        paint.shader = linearGradient
    }

    private fun setupRestOfCircle(paint: Paint) {
        paint.color = Color.RED
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
    }

    private fun setupLabelPaint() {
        labelPaint = Paint()
        labelPaint.color = Color.BLACK
        labelPaint.textSize = textSize
    }

    fun setTextSize(size: Int) {
        textSize = dpToPx(size).toFloat()
    }

    fun setupMainCircle(paint: Paint) {
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun init(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundChart)
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle(
            canvas,
            externalCirclePaint,
            getRadius(viewWidth.toFloat(), viewHeight.toFloat()) / EXTERNAL_CIRCLE_COEFFICIENT_RADIUS,
            percentExternal
        )
        drawCircle(
            canvas,
            innerCirclePaint,
            getRadius(viewWidth.toFloat(), viewHeight.toFloat()) / INNER_CIRCLE_COEFFICIENT_RADIUS,
            percentInner
        )

        drawPicker(
            canvas,
            externalCirclePickerPaint,
            getRadius(viewWidth.toFloat(), viewHeight.toFloat()) / EXTERNAL_CIRCLE_COEFFICIENT_RADIUS,
            percentExternal
        )
        drawPicker(
            canvas,
            innerCirclePickerPaint,
            getRadius(viewWidth.toFloat(), viewHeight.toFloat()) / INNER_CIRCLE_COEFFICIENT_RADIUS,
            percentInner
        )
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            getRadius(viewWidth.toFloat(), viewHeight.toFloat()) / MAIN_CIRCLE_COEFFICIENT_RADIUS,
            mainCirclePaint
        )
        val textWidth = labelPaint.measureText("This is a text.")
        canvas.drawText("This is a text.", (viewWidth / 2f) - textWidth / 2, viewHeight / 2f, labelPaint)
        canvas.drawText("This is a text.", (viewWidth / 2f) - textWidth / 2, (viewHeight / 2f) + textSize, labelPaint)

    }

    private fun drawCircle(
        canvas: Canvas,
        paint: Paint,
        circleRadius: Float,
        percentAngle: Float
    ) {
        val width = width.toFloat()
        val height = height.toFloat()

        val centerX: Float
        val centerY: Float
        val oval = RectF()

        centerX = width / 2
        centerY = height / 2

        oval.set(
            centerX - circleRadius,
            centerY - circleRadius,
            centerX + circleRadius,
            centerY + circleRadius
        )
        canvas.drawArc(oval, START_ANGLE.toFloat(), getAngleByPercent(percentAngle), false, paint)
        if (percentAngle != 100f)
            canvas.drawArc(
                oval, getAngleByPercent(percentAngle) - 90,
                360 - getAngleByPercent(percentAngle), false, restOfInnerCircle
            )
    }

    private fun drawPicker(canvas: Canvas, paint: Paint, circleRadius: Float, percentAngle: Float) {
        val angleRadians = Math.toRadians(getAngleByPercent(percentAngle).toDouble())
        val centerX = viewWidth / 2f + Math.sin(angleRadians).toFloat() * circleRadius
        val centerY = viewHeight / 2f - Math.cos(angleRadians).toFloat() * circleRadius
        canvas.drawCircle(centerX, centerY, pickerRadius, paint)
    }

    private fun getRadius(viewWidth: Float, viewHeight: Float): Float {
        return Math.min(viewHeight, viewWidth) / 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.v("Chart onMeasure w", View.MeasureSpec.toString(widthMeasureSpec))
        Log.v("Chart onMeasure h", View.MeasureSpec.toString(heightMeasureSpec))
        val parentWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = View.MeasureSpec.getSize(heightMeasureSpec)

        viewHeight = parentHeight
        viewWidth = parentWidth

        this.setMeasuredDimension(parentWidth, parentHeight)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initGradients()
    }

    private fun initGradients() {
        mainCircleGradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            Color.GREEN, Color.CYAN, Shader.TileMode.REPEAT
        )
        gradientPicker = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            Color.GREEN, Color.CYAN, Shader.TileMode.CLAMP
        )
        secondaryCircleGradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            Color.RED, Color.WHITE, Shader.TileMode.MIRROR
        )
        setPaintGradient(externalCirclePaint, mainCircleGradient)
        setPaintGradient(innerCirclePaint, mainCircleGradient)
        setPaintGradient(innerCirclePickerPaint, gradientPicker)
        setPaintGradient(externalCirclePickerPaint, gradientPicker)
        setPaintGradient(restOfExternalCircle, secondaryCircleGradient)
        setPaintGradient(restOfInnerCircle, secondaryCircleGradient)
    }

}
