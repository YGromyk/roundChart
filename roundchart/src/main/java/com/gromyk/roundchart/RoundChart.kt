package com.gromyk.roundchart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.gromyk.roundchart.helpers.PaintHelper
import com.gromyk.roundchart.helpers.ProgressCircle
import com.gromyk.roundchart.helpers.build

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
        const val MAIN_CIRCLE_COEFFICIENT = 0.75f
        const val sqrt2 = 1.41421356f
    }

    /**
     *  percent value for external circle
     * @see RoundChart.externalCircle
     **/
    var percentExternal: Float = 0f
        set(value) {
            externalCircle.progress = value
            field = value
        }

    /**
     * percent value for inner circle
     * @see RoundChart.innerCircle
     **/
    var percentInner: Float = 0f
        set(value) {
            innerCircle.progress = value
            field = value
        }

    enum class TextSize {
        SMALL,
        MEDIUM,
        LARGER
    }

    /** properties for text **/
    private var textSize = 0f
        set(value) {
            field = value
            setTextSizes(value)
        }
    private var textMargin = textSize

    init {
        setTextSize(getTextSizeByEnum(TextSize.MEDIUM.ordinal))
    }

    private var textColor: Int = Color.BLUE
    private var firstLabel = String()
    private var secondLabel = String()
    private var firstValue = String()
    private var secondValue = String()

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


    /**text labels**/
    private lateinit var firstLabelPaint: Paint
    private lateinit var firstValuePaint: Paint
    private lateinit var secondLabelPaint: Paint
    private lateinit var secondValuePaint: Paint

    private var externalCircle = ProgressCircle.build {
        isEnabled = true
        gradientFirstColorCircle = Color.GREEN
        gradientSecondColorCircle = Color.CYAN
        firstColorPickerCircle = Color.GREEN
        secondColorPickerCircle = Color.CYAN
        gradientFirstColorOverageCircle = Color.BLUE
        secondGradientColorOverageCircle = Color.RED
        getPaintsForSetShadow().forEach {
            setLayerType(LAYER_TYPE_SOFTWARE, it)
        }
    }

    private var innerCircle = ProgressCircle.build {
        coefficient = 0.85f
        gradientFirstColorCircle = Color.BLUE
        gradientSecondColorCircle = Color.CYAN
        firstColorPickerCircle = Color.BLUE
        secondColorPickerCircle = Color.CYAN
        gradientFirstColorOverageCircle = Color.BLUE
        secondGradientColorOverageCircle = Color.RED
        getPaintsForSetShadow().forEach {
            setLayerType(LAYER_TYPE_SOFTWARE, it)
        }
    }

    /** main circle paint **/
    private lateinit var mainCircleWithLabels: Paint
    private var mainCircleSolidColor = Color.GREEN
    private var mainCircleRadius = 0f
    private var mainCircleShadowRadius = 20f
    private var mainCircleSquareSide = 0f

    init {
        init(attrs)
        initView()
    }

    private fun initView() {
        initPaints()
        setupLabelText(firstLabelPaint)
        setupLabelText(secondLabelPaint)
        setupValueText(firstValuePaint)
        setupValueText(secondValuePaint)
        setupMainCircle(mainCircleWithLabels)
    }

    private fun initPaints() {
        firstLabelPaint = PaintHelper.createAntiAliasingPaint()
        secondLabelPaint = PaintHelper.createAntiAliasingPaint()
        firstValuePaint = PaintHelper.createAntiAliasingPaint()
        secondValuePaint = PaintHelper.createAntiAliasingPaint()
        mainCircleWithLabels = PaintHelper.createAntiAliasingPaint()
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

    private fun setupMainCircle(paint: Paint) {
        paint.style = Paint.Style.FILL
        paint.color = mainCircleSolidColor
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        paint.setShadowLayer(mainCircleShadowRadius, 0f, 0f, Color.BLACK)
    }

    /**
     * Set size for text in main circle in sp
     *
     * @param   size  an attribute given in SP
     * @see     RoundChart.textSize
     */
    private fun setTextSize(size: Int) {
        textSize = convertSPToPX(size).toFloat()
    }

    private fun convertSPToPX(sp: Int): Int {
        return (sp * resources.displayMetrics.scaledDensity).toInt()
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundChart)
        val textSizeEnumValue = typedArray.getInt(R.styleable.RoundChart_textSize, 14)
        setTextSize(getTextSizeByEnum(textSizeEnumValue))
        typedArray.apply {
            firstGradientColorInnerCircle =
                getColor(
                    R.styleable.RoundChart_firstGradientColorInnerCircle,
                    Color.GREEN
                )
            secondGradientColorInnerCircle =
                getColor(
                    R.styleable.RoundChart_secondGradientColorInnerCircle,
                    Color.CYAN
                )
            firstColorPickerInnerCircle =
                getColor(R.styleable.RoundChart_firstColorPickerInnerCircle, Color.GREEN)
            secondColorPickerInnerCircle =
                getColor(R.styleable.RoundChart_secondColorPickerInnerCircle, Color.CYAN)
            firstGradientColorOverageInnerCircle =
                getColor(
                    R.styleable.RoundChart_firstGradientColorOverageInnerCircle,
                    Color.RED
                )
            secondGradientColorOverageInnerCircle =
                getColor(
                    R.styleable.RoundChart_secondGradientColorOverageInnerCircle,
                    Color.WHITE
                )
            firstGradientColorExternalCircle =
                getColor(
                    R.styleable.RoundChart_firstGradientColorExternalCircle,
                    Color.GREEN
                )
            secondGradientColorExternalCircle =
                getColor(
                    R.styleable.RoundChart_secondGradientColorExternalCircle,
                    Color.CYAN
                )
            firstColorPickerExternalCircle =
                getColor(
                    R.styleable.RoundChart_firstColorPickerExternalCircle,
                    Color.GREEN
                )
            secondColorPickerExternalCircle =
                getColor(
                    R.styleable.RoundChart_secondColorPickerExternalCircle,
                    Color.CYAN
                )
            firstGradientColorOverageExternalCircle =
                getColor(
                    R.styleable.RoundChart_firstGradientColorOverageExternalCircle,
                    Color.RED
                )
            secondGradientColorOverageExternalCircle =
                getColor(
                    R.styleable.RoundChart_secondGradientColorOverageExternalCircle,
                    Color.WHITE
                )
            percentExternal = getFloat(R.styleable.RoundChart_percentExternal, 0f)
            percentInner = getFloat(R.styleable.RoundChart_percentInner, 0f)
            firstLabel = getString(R.styleable.RoundChart_firstLabel) ?: ""
            secondLabel = getString(R.styleable.RoundChart_secondLabel) ?: ""
            firstValue = getString(R.styleable.RoundChart_firstValue) ?: ""
            secondValue = getString(R.styleable.RoundChart_secondValue) ?: ""
            textColor = getColor(R.styleable.RoundChart_textColor, textColor)
            mainCircleSolidColor = getColor(R.styleable.RoundChart_mainCircleColor, Color.WHITE)
        }.recycle()
    }

    private fun getTextSizeByEnum(textSize: Int) =
        when (textSize) {
            TextSize.SMALL.ordinal -> 12
            TextSize.LARGER.ordinal -> 16
            else -> 14
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        externalCircle.draw(canvas, 0f, 0f, width.toFloat(), height.toFloat())
        innerCircle.draw(canvas, 0f, 0f, width.toFloat(), height.toFloat())
        mainCircleRadius = innerCircle.circleRadius * MAIN_CIRCLE_COEFFICIENT
        canvas.drawCircle(width / 2f, height / 2f, mainCircleRadius, mainCircleWithLabels)
        drawText(canvas, mainCircleRadius)
    }


    private fun drawText(canvas: Canvas, circleRadius: Float) {
        val x0 = width / 2f
        val y0 = height / 2f
        val cornerPoints = calculateCornerPoints(x0, y0, circleRadius)
        setMinimumTextSize()
        var startMargin = 5f
        try {
            startMargin = firstLabelPaint.measureText(firstLabel, 0, 1)
        } catch (exception: Exception) {
            Log.e(
                this::class.java.simpleName.toUpperCase(),
                "Exception occured when while measure margin"
            )
        }
        drawTextPaint(
            canvas,
            firstLabel,
            firstLabelPaint,
            cornerPoints.first.apply { x += startMargin })
        cornerPoints.first.y += textSize
        drawTextPaint(canvas, firstValue, firstValuePaint, cornerPoints.first)
        cornerPoints.first.y += textSize
        drawTextPaint(
            canvas,
            secondLabel,
            secondLabelPaint,
            cornerPoints.first
        )
        cornerPoints.first.y += textSize
        drawTextPaint(
            canvas,
            secondValue,
            secondValuePaint,
            cornerPoints.first
        )
    }

    private fun setMinimumTextSize() {
        val minTextSize = getMinTextSizeAndMaxHeight()
        this.textSize = minTextSize.first
        textMargin = minTextSize.second + 5f
    }

    private fun setTextSizes(textSize: Float) {
        if (::firstValuePaint.isInitialized) firstValuePaint.textSize = textSize
        if (::firstLabelPaint.isInitialized) firstLabelPaint.textSize = textSize
        if (::secondValuePaint.isInitialized) secondValuePaint.textSize = textSize
        if (::secondLabelPaint.isInitialized) secondLabelPaint.textSize = textSize
    }

    private fun getMinTextSizeAndMaxHeight(): Pair<Float, Float> {
        val maxWidth = mainCircleSquareSide * 2
        val list = listOf(
            calculateTextSize(firstValuePaint, firstValue, maxWidth),
            calculateTextSize(firstLabelPaint, firstLabel, maxWidth),
            calculateTextSize(secondValuePaint, secondValue, maxWidth),
            calculateTextSize(secondLabelPaint, secondLabel, maxWidth)
        )
        return Pair(list.map { it.first }.min()!!, list.map { it.second }.max()!!)
    }

    private fun calculateCornerPoints(x0: Float, y0: Float, radius: Float): Pair<PointF, PointF> {
        mainCircleSquareSide = radius / sqrt2
        return Pair(
            PointF(x0 - mainCircleSquareSide, y0 - mainCircleSquareSide + textSize),
            PointF(x0 - mainCircleSquareSide, y0 + mainCircleSquareSide - textSize)
        )
    }

    private fun drawTextPaint(canvas: Canvas, text: String, paint: Paint, coordinates: PointF) {
        val textWidth = paint.measureText(text)
        canvas.drawText(text, coordinates.x + textWidth, coordinates.y, paint)
    }

    private fun calculateTextSize(
        textPaint: Paint,
        text: String,
        maxWidth: Float
    ): Pair<Float, Float> {
        textPaint.textSize = 100f
        var textWidth = textPaint.measureText(text)

        while (textWidth > maxWidth) {
            textPaint.textSize = textPaint.textSize - 2
            textWidth = textPaint.measureText(text)
        }
        val bound = Rect()
        textPaint.getTextBounds(text, 0, text.length, bound)
        return Pair(textPaint.textSize, bound.height().toFloat())
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.v("Chart onMeasure w", MeasureSpec.toString(widthMeasureSpec))
        Log.v("Chart onMeasure h", MeasureSpec.toString(heightMeasureSpec))
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        this.setMeasuredDimension(parentWidth, parentHeight)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
