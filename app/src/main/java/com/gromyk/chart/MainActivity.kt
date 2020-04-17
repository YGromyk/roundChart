package com.gromyk.chart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        roundChart.setClickListener()
    }
}

fun com.gromyk.roundchart.RoundChart.setClickListener() {
    setOnClickListener {
        with(this) {
            val randomGradExternal = ((0..99).random().toFloat())
            val randomGradInner = ((0..99).random().toFloat())
            val random = ((50..100).random().toFloat())
            percentExternal = randomGradExternal + random
            percentInner = randomGradInner + random
        }
        this.invalidate()
    }
}
