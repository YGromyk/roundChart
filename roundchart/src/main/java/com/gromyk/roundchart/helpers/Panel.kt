package com.gromyk.roundchart.helpers

import android.graphics.Canvas

/**
 * Created by Yuriy Gromyk on 11/7/18.
 */
class Panel(
    val width: Float,
    val height: Float,
    val locationX: Int, // 0-100
    val locationY: Int, // 0-100
    val components: MutableList<DrawableComponent>
): DrawableComponent {

    override fun draw(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        val drawX = width / 100 * locationX
        val drawY = height / 100 * locationY
        for (component in components) {
            component.draw(canvas, drawX, drawY, this.width, this.height)
        }
    }
}