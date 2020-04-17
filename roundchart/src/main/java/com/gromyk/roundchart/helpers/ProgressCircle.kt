package com.gromyk.roundchart.helpers

import android.graphics.*
import android.util.Log
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by Yuriy Gromyk on 11/7/18.
 */
class ProgressCircle(var coefficient: Float = 1.0f) : DrawableComponent {

    companion object {
        private const val START_ANGLE = -90f
        private const val RADIUS_COEFFICIENT = 0.9f
        private const val PICKER_COEFFICIENT = 0.05f
        private const val PICKER_BORDER_COEFFICIENT = 0.075f
        private const val STROKE_WIDTH_COEFFICIENT = PICKER_BORDER_COEFFICIENT
        private const val STROKE_OVERAGE_COEFFICIENT = 0.005f
    }

    /** progress for circle in percent **/
    var progress: Float = 0f // 0-100
    /****/
    var circleRadius: Float = 0f
        private set
    /** property that controls drawing of a circle **/
    var isEnabled = true
    /**colors for the circle**/
    var gradientFirstColorCircle: Int = Color.GREEN
    var gradientSecondColorCircle: Int = Color.CYAN
    var firstColorPickerCircle: Int = Color.GREEN
    var secondColorPickerCircle: Int = Color.CYAN
    var gradientFirstColorOverageCircle: Int = Color.WHITE
    var secondGradientColorOverageCircle: Int = Color.RED
    /**shadow radius for all views**/
    var shadowRadius = 20f
    /** paints**/
    private lateinit var circlePaint: Paint
    private lateinit var overageCircle: Paint
    private lateinit var circlePickerPaint: Paint
    private lateinit var circlePickerBorder: Paint
    /**gradients for  circle**/
    private lateinit var gradientPicker: LinearGradient
    private lateinit var gradientCircle: LinearGradient
    private lateinit var gradientOverageCircle: LinearGradient
    private lateinit var defaultGrayGradient: LinearGradient

    init {
        initPaints()
        setupPaints()
    }

    private fun initPaints() {
        circlePaint = PaintHelper.createAntiAliasingPaint()
        overageCircle = PaintHelper.createAntiAliasingPaint()
        circlePickerBorder = PaintHelper.createAntiAliasingPaint()
        circlePickerPaint = PaintHelper.createAntiAliasingPaint()
    }

    private fun setupPaints() {
        setupMainCircle(circlePaint)
        setupOverageCircle(overageCircle)
        setupPickerBorderPaint(circlePickerPaint)
        setupPickerBorderPaint(circlePickerBorder)
    }

    private fun setupMainCircle(paint: Paint) {
        paint.color = gradientFirstColorCircle
        paint.style = Paint.Style.STROKE
    }

    private fun setupOverageCircle(paint: Paint) {
        paint.color = gradientFirstColorOverageCircle
        paint.style = Paint.Style.STROKE
    }

    private fun setupPickerBorderPaint(paint: Paint) {
        paint.setShadowLayer(shadowRadius, 0f, 0f, Color.BLACK)
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        if (!isEnabled) return
        val startTime = System.currentTimeMillis()

        circleRadius = (Math.min(width / 2, height / 2) * RADIUS_COEFFICIENT) * coefficient
        initGradients(height * coefficient)
        setGradientsForPaints()
        setStrokeWidthOverage(circleRadius)

        val oval = RectF()
        val center = PointF(x + width / 2f, y + height / 2)
        oval.set(
            center.x - circleRadius,
            center.y - circleRadius,
            center.x + circleRadius,
            center.y + circleRadius
        )
        val (startAngleMainCircle, sweepAngle) = calculateStartAndSweepAngles()
        canvas.drawArc(oval, startAngleMainCircle, sweepAngle, false, circlePaint)
        canvas.drawArc(
            oval,
            PaintHelper.getAngleByPercent(progress) - 90,
            360 - PaintHelper.getAngleByPercent(progress),
            false,
            overageCircle
        )
        drawPicker(canvas, width, height, circleRadius)
        Log.e(this::class.java.simpleName, "time of drawing: ${System.currentTimeMillis()- startTime}")
    }

    private fun calculateStartAndSweepAngles(): Pair<Float, Float> {
        var startAngleMainCircle = START_ANGLE
        var sweepAngle = PaintHelper.getAngleByPercent(progress)
        var progressForView = progress
        if (progress > 100f) {
            while (progressForView >= 100)
                progressForView -= 100
            startAngleMainCircle = PaintHelper.getAngleByPercent(progressForView) - 90
            sweepAngle = 360 - startAngleMainCircle - 90
        }
        return Pair(startAngleMainCircle, sweepAngle)
    }

    private fun drawPicker(
        canvas: Canvas,
        width: Float,
        height: Float,
        circleRadius: Float
    ) {
        val angleRadians = Math.toRadians(PaintHelper.getAngleByPercent(progress).toDouble())
        val centerX = width / 2f + sin(angleRadians).toFloat() * circleRadius
        val centerY = height / 2f - cos(angleRadians).toFloat() * circleRadius
        canvas.drawCircle(centerX, centerY, circleRadius * PICKER_BORDER_COEFFICIENT, circlePickerBorder)
        canvas.drawCircle(centerX, centerY, circleRadius * PICKER_COEFFICIENT, circlePickerPaint)
    }


    private fun setPaintGradient(paint: Paint, linearGradient: LinearGradient) {
        paint.setShadowLayer(shadowRadius, 0f, 0f, Color.BLACK)
        paint.shader = linearGradient
    }

    private fun initGradients(height: Float) {
        gradientPicker = PaintHelper.createLinearGradientByColors(
            firstColorPickerCircle, secondColorPickerCircle, Shader.TileMode.CLAMP, height
        )
        gradientCircle = PaintHelper.createLinearGradientByColors(
            gradientFirstColorCircle, gradientSecondColorCircle, Shader.TileMode.CLAMP, height
        )
        gradientOverageCircle = PaintHelper.createLinearGradientByColors(
            gradientFirstColorOverageCircle, secondGradientColorOverageCircle, Shader.TileMode.CLAMP, height
        )

        defaultGrayGradient =
                PaintHelper.createLinearGradientByColors(Color.DKGRAY, Color.DKGRAY, Shader.TileMode.CLAMP, height)
    }

    private fun setGradientsForPaints() {
        PaintHelper.setPaintGradient(circlePaint, gradientPicker, shadowRadius)
        PaintHelper.setPaintGradient(overageCircle, gradientCircle, shadowRadius)
        PaintHelper.setPaintGradient(circlePickerPaint, gradientOverageCircle, shadowRadius)
    }

    private fun setStrokeWidthOverage(circleRadius: Float) {
        if (progress > 100f) {
            overageCircle.strokeWidth = circleRadius * STROKE_WIDTH_COEFFICIENT
            circlePaint.strokeWidth = circleRadius * STROKE_WIDTH_COEFFICIENT
            setPaintGradient(overageCircle, gradientOverageCircle)
            setPaintGradient(circlePickerPaint, gradientOverageCircle)
        } else {
            overageCircle.strokeWidth = circleRadius * STROKE_OVERAGE_COEFFICIENT
            circlePaint.strokeWidth = circleRadius * STROKE_WIDTH_COEFFICIENT
            setPaintGradient(overageCircle, defaultGrayGradient)
            setPaintGradient(circlePickerPaint, gradientCircle)
        }
    }

    fun getPaintsForSetShadow(): List<Paint> {
        val paints = mutableListOf<Paint>()
        paints.add(circlePaint)
        paints.add(overageCircle)
        paints.add(circlePickerBorder)
        return paints
    }
}

fun ProgressCircle.Companion.build(block: ProgressCircle.() -> Unit): ProgressCircle {
    val progressCircle = ProgressCircle()
    block(progressCircle)
    return progressCircle
}
