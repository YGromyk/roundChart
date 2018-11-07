package com.gromyk.chart.helpers

import android.graphics.Canvas

/**
 * Created by Yuriy Gromyk on 11/7/18.
 */
interface DrawableComponent {
    fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float)
}