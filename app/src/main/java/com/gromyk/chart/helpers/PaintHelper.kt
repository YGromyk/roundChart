package com.gromyk.chart.helpers

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.view.View

/**
 * Created by Yuriy Gromyk on 11/7/18.
 */

object PaintHelper {
    fun setPaintGradient(paint: Paint, linearGradient: LinearGradient, shadowRadius: Float) {
        paint.setShadowLayer(shadowRadius, 0f, 0f, Color.BLACK)
        paint.shader = linearGradient
    }

    fun createAntiAliasingPaint(): Paint {
        val paint = Paint()
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.isAntiAlias = true
        return paint
    }


    fun createLinearGradientByColors(
        firstColor: Int,
        secondColor: Int,
        gradientMode: Shader.TileMode,
        height: Float
    ) =
        LinearGradient(
            0f, 0f, 0f, height,
            firstColor, secondColor, gradientMode
        )

    fun getAngleByPercent(percent: Float) = (360f / 100f) * percent

}