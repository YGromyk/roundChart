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
        public fun getAngleByPercent(percent: Float): Float {
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

    var percentExternal: Float = 0f
    var percentInner: Float = 100f

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundChart)
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawExternalCircle(canvas)
        drawInnerCircle(canvas)
        drawPicker(canvas)
    }

    private fun drawExternalCircle(canvas: Canvas) {
        val radius = getRadius(viewWidth.toFloat(), viewHeight.toFloat())

        externalBorderCirclePath.reset()
        externalBorderCirclePath.addCircle(
            viewWidth.toFloat() / 2,
            viewHeight.toFloat() / 2,
            radius,
            Path.Direction.CW
        )

        val paint = Paint()
        paint.color = Color.GREEN
        paint.strokeWidth = 24f
        paint.style = Paint.Style.STROKE

        val centerX: Float = viewWidth.toFloat() / 2
        val centerY: Float = viewHeight.toFloat() / 2
        val oval = RectF()
        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            Color.GREEN,
            Color.CYAN,
            Shader.TileMode.CLAMP
        )
        paint.style = Paint.Style.STROKE

        oval.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        canvas.drawArc(
            oval,
            START_ANGLE.toFloat(),
            getAngleByPercent(percentExternal),
            false,
            paint
        )
        canvas.drawCircle(viewWidth / 2f, (viewHeight / 2f) - radius, 25f, Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLACK
        })
        canvas.drawCircle(viewWidth / 2f, (viewHeight / 2f) + radius, 25f, Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLACK
        })

        canvas.drawCircle(0f, viewHeight / 2f, 25f, Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLACK
        })

        canvas.drawCircle(viewWidth.toFloat(), viewHeight / 2f, 25f, Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLACK
        })
    }

    private fun drawInnerCircle(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = getRadius(viewWidth.toFloat(), viewHeight.toFloat()) / 2f

        innerBorderCirclePath.reset()
        innerBorderCirclePath.addCircle(
            width / 2,
            height / 2,
            radius,
            Path.Direction.CW
        )

        val paint = Paint()
        paint.setShadowLayer(50f, 0f, 0f, Color.BLACK)
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
        paint.color = Color.CYAN
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            getHeight().toFloat(),
            Color.GREEN,
            Color.CYAN,
            Shader.TileMode.CLAMP
        )

        val centerX: Float
        val centerY: Float
        val oval = RectF()
        paint.style = Paint.Style.FILL

        centerX = width / 2
        centerY = height / 2

        oval.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        canvas.drawArc(oval, START_ANGLE.toFloat(), getAngleByPercent(percentExternal.toFloat()), true, paint)
    }

    private fun drawCircle(
        canvas: Canvas,
        circleRadius: Float,
        percentAngle: Float,
        circlePath: Path
    ) {
        val width = width.toFloat()
        val height = height.toFloat()

        circlePath.reset()
        circlePath.addCircle(
            width / 2,
            height / 2,
            circleRadius,
            Path.Direction.CW
        )

        val paint = Paint()
        paint.setShadowLayer(50f, 0f, 0f, Color.BLACK)
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        paint.color = Color.CYAN
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            getHeight().toFloat(),
            Color.GREEN,
            Color.CYAN,
            Shader.TileMode.CLAMP
        )

        val centerX: Float
        val centerY: Float
        val oval = RectF()
        paint.style = Paint.Style.FILL

        centerX = width / 2
        centerY = height / 2

        oval.set(
            centerX - circleRadius,
            centerY - circleRadius,
            centerX + circleRadius,
            centerY + circleRadius
        )
        canvas.drawArc(oval, START_ANGLE.toFloat(), getAngleByPercent(percentAngle), true, paint)

    }


    private fun drawPicker(
        canvas: Canvas,
        circleRadius: Float,
        percentAngle: Float,
        pickerPath: Path
        ) {
//        val circleRadius = getRadius(viewWidth.toFloat(), viewHeight.toFloat())
        val angleRadians = Math.toRadians(getAngleByPercent(percentAngle).toDouble())
        val centerX =
            viewWidth / 2f + Math.sin(angleRadians).toFloat() * circleRadius
        val centerY =
            viewHeight / 2f - Math.cos(angleRadians).toFloat() * circleRadius

        pickerPath.reset()
        pickerPath.addCircle(
            centerX,
            centerY,
            5f,
            Path.Direction.CW
        )


        val paint = Paint()
        paint.color = Color.CYAN
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            Color.BLACK,
            Color.RED,
            Shader.TileMode.MIRROR
        )
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
    }

}
