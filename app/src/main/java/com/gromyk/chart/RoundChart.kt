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

        @Throws(IllegalArgumentException::class)
        fun getAngleByPercent(percent: Float): Float {
            if (percent > 100f || percent < 0f)
                throw IllegalArgumentException("Value " + percent.toString() + " cannot be used as percent!")
            return (360f / 100f) * percent
        }
    }

    private var viewWidth = 0
    private var viewHeight = 0

    private val externalBorderCirclePath = Path()
    private val innerBorderCirclePath = Path()
    private val externalCirclePicker = Path()
    private val innerCirclePicker = Path()
    private val mainCircle = Path()

    private lateinit var gradientPicker: LinearGradient
    private lateinit var gradientCircle: LinearGradient


    var percentExternal: Float = 0f
    var percentInner: Float = 0f

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundChart)
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        drawExternalCircle(canvas)
        drawCircle(
            canvas,
            getRadius(viewWidth.toFloat(), viewHeight.toFloat()),
            percentExternal,
            externalBorderCirclePath,
            Paint.Style.STROKE,
            gradientCircle
        )
        drawCircle(
            canvas,
            getRadius(viewWidth.toFloat(), viewHeight.toFloat()) / 1.25f,
            percentInner,
            innerBorderCirclePath,
            Paint.Style.STROKE,
            gradientCircle
        )

        drawCircle(
            canvas,
            getRadius(viewWidth.toFloat(), viewHeight.toFloat()) / 2f,
            100f,
            mainCircle,
            Paint.Style.FILL,
            gradientCircle
        )

        drawPicker(
            canvas,
            getRadius(viewWidth.toFloat(), viewHeight.toFloat()),
            percentExternal,
            externalCirclePicker,
            gradientPicker
        )
        drawPicker(
            canvas,
            getRadius(viewWidth.toFloat(), viewHeight.toFloat()) / 1.25f,
            percentInner,
            innerCirclePicker,
            gradientPicker
        )
    }

    private fun drawCircle(
        canvas: Canvas, circleRadius: Float, percentAngle: Float,
        circlePath: Path, style: Paint.Style, linearGradient: LinearGradient) {
        val width = width.toFloat()
        val height = height.toFloat()

        circlePath.reset()
        circlePath.addCircle(width / 2, height / 2, circleRadius, Path.Direction.CW)

        val paint = Paint()
        paint.setShadowLayer(50f, 0f, 0f, Color.BLACK)
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        paint.color = Color.CYAN
        paint.strokeWidth = 20f
        paint.style = Paint.Style.STROKE
        paint.shader = linearGradient

        val centerX: Float
        val centerY: Float
        val oval = RectF()
        paint.style = style

        centerX = width / 2
        centerY = height / 2

        oval.set(
            centerX - circleRadius,
            centerY - circleRadius,
            centerX + circleRadius,
            centerY + circleRadius
        )
        canvas.drawArc(oval, START_ANGLE.toFloat(), getAngleByPercent(percentAngle), false, paint)
        paint.color = Color.RED
        paint.strokeWidth = 20f
        paint.style = Paint.Style.STROKE
        paint.shader = LinearGradient(
            0f, 0f, 0f, getHeight().toFloat(),
            Color.RED, Color.WHITE, Shader.TileMode.MIRROR
        )
        canvas.drawArc(oval, getAngleByPercent(percentAngle) - 90, 360 - getAngleByPercent(percentAngle), false, paint)
    }

    private fun drawPicker(canvas: Canvas, circleRadius: Float, percentAngle: Float, pickerPath: Path, linearGradient: LinearGradient) {
        val angleRadians = Math.toRadians(getAngleByPercent(percentAngle).toDouble())
        val centerX = viewWidth / 2f + Math.sin(angleRadians).toFloat() * circleRadius
        val centerY = viewHeight / 2f - Math.cos(angleRadians).toFloat() * circleRadius

        pickerPath.reset()
        pickerPath.addCircle(
            centerX, centerY,
            5f, Path.Direction.CW
        )

        val paint = Paint()
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
        paint.shader = linearGradient
        paint.style = Paint.Style.FILL
        val radius = 25f
        canvas.drawCircle(centerX, centerY, radius, paint)
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
        gradientCircle = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            Color.GREEN, Color.CYAN, Shader.TileMode.CLAMP
        )
        gradientPicker = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            Color.GREEN, Color.CYAN, Shader.TileMode.CLAMP
        )
    }

}
