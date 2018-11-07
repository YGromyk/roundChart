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
class RoundChartOld @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val START_ANGLE = -90
        private const val pickerRadius = 15f
        private const val pickerBorderRadius = pickerRadius + 10
        private const val EXTERNAL_CIRCLE_COEFFICIENT_RADIUS = 1.05f
        private const val INNER_CIRCLE_COEFFICIENT_RADIUS = 1.25f
        private const val MAIN_CIRCLE_COEFFICIENT_RADIUS = 2f

        @Throws(IllegalArgumentException::class)
        fun getAngleByPercent(percent: Float) = (360f / 100f) * percent

        fun createAntiAliasingPaint(): Paint {
            val paint = Paint()
            paint.flags = Paint.ANTI_ALIAS_FLAG
            paint.isAntiAlias = true
            return paint
        }
    }


    enum class TextSize {
        SMALL,
        MEDIUM,
        LARGER
    }

    private var CW = true
    private var shadowRadius = 20f
    private var strokeWidth = 20f
    private var strokeWidthOfOverage = 5f
    private var textSize = 30f
    private var textColor: Int = Color.BLACK

    private var firstLabel = String()
    private var secondLabel = String()
    private var firstValue = String()
    private var secondValue = String()

    private var externalRadius = 0f
    private var innerRadius = 0f
    private var mainRadius = 0f
    private lateinit var center: Point

    /**colors for the external circle**/
    private var firstGradientColorExternalCircle: Int = Color.GREEN
    private var secondGradientColorExternalCircle: Int = Color.CYAN
    private var firstColorPickerExternalCircle: Int = Color.GREEN
    private var secondColorPickerExternalCircle: Int = Color.CYAN
    private var firstGradientColorOverageExternalCircle: Int = Color.WHITE
    private var secondGradientColorOverageExternalCircle: Int = Color.RED

    /** colors for the inner circle**/
    private var firstGradientColorInnerCircle: Int = Color.GREEN
    private var secondGradientColorInnerCircle: Int = Color.CYAN
    private var firstColorPickerInnerCircle: Int = Color.GREEN
    private var secondColorPickerInnerCircle: Int = Color.CYAN
    private var firstGradientColorOverageInnerCircle: Int = Color.WHITE
    private var secondGradientColorOverageInnerCircle: Int = Color.RED

    /**percent value for external circle**/
    var percentExternal: Float = 0f
    /**percent value for inner circle**/
    var percentInner: Float = 0f

    /**external circle paints**/
    private lateinit var externalCirclePaint: Paint
    private lateinit var innerCirclePaint: Paint
    private lateinit var externalCirclePickerPaint: Paint
    private lateinit var externalPickerBorder: Paint
    /**inner circle paints**/
    private lateinit var innerCirclePickerPaint: Paint
    private lateinit var innerPickerBorder: Paint
    private lateinit var overageOfExternalCirclePaint: Paint
    private lateinit var overageOfInnerCirclePaint: Paint
    /**external circle paints, where are all labels with text**/
    private lateinit var mainCirclePaint: Paint
    /**text labels**/
    private lateinit var firstLabelPaint: Paint
    private lateinit var firstValuePaint: Paint
    private lateinit var secondLabelPaint: Paint
    private lateinit var secondValuePaint: Paint
    /**gradients for external circle**/
    private lateinit var externalGradientPicker: LinearGradient
    private lateinit var externalCircleGradient: LinearGradient
    private lateinit var externalOverageCircleGradient: LinearGradient
    /**gradients for inner circle**/
    private lateinit var innerGradientPicker: LinearGradient
    private lateinit var innerCircleGradient: LinearGradient
    private lateinit var innerOverageCircleGradient: LinearGradient
    private lateinit var defaultGrayGradient: LinearGradient

    init {
        init(attrs)
        initView()
    }

    private fun initView() {
        initPaints()
        setupCirclePaint(innerCirclePaint, Paint.Style.STROKE)
        setupCirclePaint(externalCirclePaint, Paint.Style.STROKE)
        setupOverageOfCircle(overageOfExternalCirclePaint)
        setupOverageOfCircle(overageOfInnerCirclePaint)
        setupMainCircle(mainCirclePaint)
        setupPickerBorderPaint(externalPickerBorder)
        setupPickerBorderPaint(innerPickerBorder)
        setupLabelText(firstLabelPaint)
        setupLabelText(secondLabelPaint)
        setupValueText(firstValuePaint)
        setupValueText(secondValuePaint)
    }

    private fun initPaints() {
        externalCirclePaint = createAntiAliasingPaint()
        innerCirclePaint = createAntiAliasingPaint()
        externalCirclePickerPaint = createAntiAliasingPaint()
        innerCirclePickerPaint = createAntiAliasingPaint()
        mainCirclePaint = createAntiAliasingPaint()
        overageOfExternalCirclePaint = createAntiAliasingPaint()
        overageOfInnerCirclePaint = createAntiAliasingPaint()
        externalPickerBorder = createAntiAliasingPaint()
        innerCirclePaint = createAntiAliasingPaint()
        externalPickerBorder = createAntiAliasingPaint()
        innerPickerBorder = createAntiAliasingPaint()
        firstLabelPaint = createAntiAliasingPaint()
        firstValuePaint = createAntiAliasingPaint()
        secondLabelPaint = createAntiAliasingPaint()
        secondValuePaint = createAntiAliasingPaint()
    }

    private fun setupLabelText(paint: Paint) {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = textColor
        paint.textSize = textSize
        paint.textAlign = Paint.Align.RIGHT
    }

    private fun setupValueText(paint: Paint) {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = textColor
        paint.textSize = textSize
        paint.textAlign = Paint.Align.RIGHT
    }

    private fun setupCirclePaint(paint: Paint, style: Paint.Style) {
        paint.textSize = textSize
        paint.color = Color.CYAN
        paint.style = style
        paint.strokeWidth = strokeWidth
    }

    private fun setStrokeWidthOverage(
        circle: Paint,
        overageCircle: Paint,
        percentValue: Float,
        overageCircleGradient: LinearGradient,
        picker: Paint,
        defaultPickerGradient: LinearGradient
    ) {
        if (percentValue > 100f) {
            overageCircle.strokeWidth = strokeWidth
            setPaintGradient(overageCircle, overageCircleGradient)
            setPaintGradient(picker, overageCircleGradient)
        } else {
            overageCircle.strokeWidth = strokeWidthOfOverage
            circle.strokeWidth = strokeWidth
            setPaintGradient(overageCircle, defaultGrayGradient)
            setPaintGradient(picker, defaultPickerGradient)
        }
    }

    private fun setPaintGradient(paint: Paint, linearGradient: LinearGradient) {
        paint.setShadowLayer(shadowRadius, 0f, 0f, Color.BLACK)
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        paint.shader = linearGradient
    }

    private fun setupOverageOfCircle(paint: Paint) {
        paint.color = Color.RED
        paint.strokeWidth = strokeWidthOfOverage
        paint.style = Paint.Style.STROKE
    }

    private fun setupPickerBorderPaint(paint: Paint) {
        paint.setShadowLayer(shadowRadius, 0f, 0f, Color.BLACK)
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
    }

    /**
     * Set size for text in main circle in sp
     *
     * @param  size  an attribute given in SP
     * @see         RoundChartOld.textSize
     */
    private fun setTextSize(size: Int) {
        textSize = convertSPToPX(size).toFloat()
    }

    private fun setupMainCircle(paint: Paint) {
        paint.setShadowLayer(shadowRadius, 0f, 0f, Color.BLACK)
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
    }

    private fun convertSPToPX(sp: Int): Int {
        return (sp * resources.displayMetrics.scaledDensity).toInt()
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundChart)
        val textSizeEnumValue = typedArray.getInt(R.styleable.RoundChart_textSize, 14)
        setTextSize(getTextSizeByEnum(textSizeEnumValue))
        firstGradientColorInnerCircle =
                typedArray.getColor(R.styleable.RoundChart_firstGradientColorInnerCircle, Color.GREEN)
        secondGradientColorInnerCircle =
                typedArray.getColor(R.styleable.RoundChart_secondGradientColorInnerCircle, Color.CYAN)
        firstColorPickerInnerCircle =
                typedArray.getColor(R.styleable.RoundChart_firstColorPickerInnerCircle, Color.GREEN)
        secondColorPickerInnerCircle =
                typedArray.getColor(R.styleable.RoundChart_secondColorPickerInnerCircle, Color.CYAN)
        firstGradientColorOverageInnerCircle =
                typedArray.getColor(R.styleable.RoundChart_firstGradientColorOverageInnerCircle, Color.RED)
        secondGradientColorOverageInnerCircle =
                typedArray.getColor(R.styleable.RoundChart_secondGradientColorOverageInnerCircle, Color.WHITE)
        firstGradientColorExternalCircle =
                typedArray.getColor(R.styleable.RoundChart_firstGradientColorExternalCircle, Color.GREEN)
        secondGradientColorExternalCircle =
                typedArray.getColor(R.styleable.RoundChart_secondGradientColorExternalCircle, Color.CYAN)
        firstColorPickerExternalCircle =
                typedArray.getColor(R.styleable.RoundChart_firstColorPickerExternalCircle, Color.GREEN)
        secondColorPickerExternalCircle =
                typedArray.getColor(R.styleable.RoundChart_secondColorPickerExternalCircle, Color.CYAN)
        firstGradientColorOverageExternalCircle =
                typedArray.getColor(R.styleable.RoundChart_firstGradientColorOverageExternalCircle, Color.RED)
        secondGradientColorOverageExternalCircle =
                typedArray.getColor(R.styleable.RoundChart_secondGradientColorOverageExternalCircle, Color.WHITE)
        percentExternal = typedArray.getFloat(R.styleable.RoundChart_percentExternal, 0f)
        percentInner = typedArray.getFloat(R.styleable.RoundChart_percentInner, 0f)
        firstLabel = typedArray.getString(R.styleable.RoundChart_firstLabel) ?: ""
        secondLabel = typedArray.getString(R.styleable.RoundChart_secondLabel) ?: ""
        firstValue = typedArray.getString(R.styleable.RoundChart_firstValue) ?: ""
        secondValue = typedArray.getString(R.styleable.RoundChart_secondValue) ?: ""
        textColor = typedArray.getColor(R.styleable.RoundChart_textColor, Color.BLACK)

        typedArray.recycle()
    }

    private fun getTextSizeByEnum(textSize: Int) =
        when (textSize) {
            TextSize.SMALL.ordinal -> 12
            TextSize.LARGER.ordinal -> 16
            else -> 14
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initSizesOfView()
        setStrokeWidthOverage(
            externalCirclePaint,
            overageOfExternalCirclePaint,
            percentExternal,
            externalOverageCircleGradient,
            externalCirclePickerPaint,
            externalGradientPicker
        )
        setStrokeWidthOverage(
            innerCirclePaint,
            overageOfInnerCirclePaint,
            percentInner,
            innerOverageCircleGradient,
            innerCirclePickerPaint,
            innerGradientPicker
        )
        drawCircle(
            canvas,
            externalCirclePaint,
            overageOfExternalCirclePaint,
            externalRadius,
            percentExternal
        )
        drawCircle(
            canvas,
            innerCirclePaint,
            overageOfInnerCirclePaint,
            innerRadius,
            percentInner
        )

        drawPicker(
            canvas,
            externalCirclePickerPaint,
            externalPickerBorder,
            externalRadius,
            percentExternal
        )
        drawPicker(
            canvas,
            innerCirclePickerPaint,
            innerPickerBorder,
            innerRadius,
            percentInner
        )
        canvas.drawCircle(
            center.x.toFloat(),
            center.y.toFloat(),
            mainRadius,
            mainCirclePaint
        )
        drawText(canvas)

    }

    private fun drawText(canvas: Canvas) {
//        val x0 = width / 2f
//        val y0 = height / 2f + getRadius(width, height)
//        lateinit var coordinates: Point
//        coordinates = drawTextPaint(canvas, firstLabel, firstLabelPaint, Point(x0.toInt(), (y0 - textSize * 2).toInt()))
//        coordinates =
//                drawTextPaint(canvas, firstValue, firstValuePaint, coordinates.apply { y += textSize.toInt() })
//        coordinates =
//                drawTextPaint(canvas, secondLabel, secondLabelPaint, coordinates.apply { y += textSize.toInt() })
//        drawTextPaint(canvas, secondValue, secondValuePaint, coordinates.apply { y += (textSize * 2).toInt() })
    }


    private fun drawTextPaint(canvas: Canvas, text: String, paint: Paint, coordinates: Point): Point {
        val textWidth = paint.measureText(text)
        canvas.drawText(text, coordinates.x - (textWidth), coordinates.y.toFloat(), paint)
        return coordinates
    }

    private fun drawCircle(
        canvas: Canvas,
        circle: Paint,
        overageCircle: Paint,
        circleRadius: Float,
        percentAngle: Float
    ) {

        val oval = RectF()

        oval.set(
            center.x.toFloat() - circleRadius,
            center.y.toFloat() - circleRadius,
            center.x.toFloat() + circleRadius,
            center.y.toFloat() + circleRadius
        )
        canvas.drawArc(oval, START_ANGLE.toFloat(), getAngleByPercent(percentAngle), false, circle)
        canvas.drawArc(
            oval, getAngleByPercent(percentAngle) - 90,
            360 - getAngleByPercent(percentAngle), false, overageCircle
        )
    }

    private fun drawPicker(
        canvas: Canvas,
        picker: Paint,
        pickerBorder: Paint,
        circleRadius: Float,
        percentAngle: Float
    ) {
        val angleRadians = Math.toRadians(getAngleByPercent(percentAngle).toDouble())
        val centerX = width / 2f + Math.sin(angleRadians).toFloat() * circleRadius
        val centerY = height / 2f - Math.cos(angleRadians).toFloat() * circleRadius
        canvas.drawCircle(centerX, centerY, pickerBorderRadius, pickerBorder)
        canvas.drawCircle(centerX, centerY, pickerRadius, picker)
    }

    private fun getRadius(viewWidth: Float, viewHeight: Float): Float {
        return Math.min(viewHeight, viewWidth) / 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.v("Chart onMeasure w", View.MeasureSpec.toString(widthMeasureSpec))
        Log.v("Chart onMeasure h", View.MeasureSpec.toString(heightMeasureSpec))
        val parentWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        this.setMeasuredDimension(parentWidth, parentHeight)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initGradients()
    }

    private fun initGradients() {
        externalCircleGradient = createLinearGradientByColors(
            firstGradientColorExternalCircle, secondGradientColorExternalCircle, Shader.TileMode.MIRROR
        )
        externalGradientPicker = createLinearGradientByColors(
            firstColorPickerExternalCircle, secondColorPickerExternalCircle, Shader.TileMode.MIRROR
        )
        externalOverageCircleGradient = createLinearGradientByColors(
            firstGradientColorOverageExternalCircle, secondGradientColorOverageExternalCircle, Shader.TileMode.MIRROR
        )
        innerCircleGradient = createLinearGradientByColors(
            firstGradientColorInnerCircle, secondGradientColorInnerCircle, Shader.TileMode.MIRROR
        )
        innerGradientPicker = createLinearGradientByColors(
            firstColorPickerInnerCircle, secondColorPickerInnerCircle, Shader.TileMode.MIRROR
        )
        innerOverageCircleGradient = createLinearGradientByColors(
            firstGradientColorOverageInnerCircle, secondGradientColorOverageInnerCircle, Shader.TileMode.MIRROR
        )

        defaultGrayGradient = createLinearGradientByColors(
            Color.GRAY, Color.GRAY, Shader.TileMode.MIRROR
        )

        setPaintGradient(externalCirclePaint, externalCircleGradient)
        setPaintGradient(innerCirclePaint, innerCircleGradient)
        setPaintGradient(innerCirclePickerPaint, innerCircleGradient)
        setPaintGradient(externalCirclePickerPaint, externalGradientPicker)
        setPaintGradient(overageOfExternalCirclePaint, externalOverageCircleGradient)
        setPaintGradient(overageOfInnerCirclePaint, innerOverageCircleGradient)
    }

    private fun initSizesOfView() {
        externalRadius = getRadius(width.toFloat(), height.toFloat()) / EXTERNAL_CIRCLE_COEFFICIENT_RADIUS
        innerRadius = getRadius(width.toFloat(), height.toFloat()) / INNER_CIRCLE_COEFFICIENT_RADIUS
        mainRadius = getRadius(width.toFloat(), height.toFloat()) / MAIN_CIRCLE_COEFFICIENT_RADIUS
        center = Point(width / 2, height / 2)
    }

    private fun createLinearGradientByColors(firstColor: Int, secondColor: Int, gradientMode: Shader.TileMode) =
        LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            firstColor, secondColor, gradientMode
        )
}
