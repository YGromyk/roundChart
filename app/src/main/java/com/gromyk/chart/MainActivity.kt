package com.gromyk.chart

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firstCircle.setOnClickListener {
            with(firstCircle){
                val randomGradExternal = ((0..99).random().toFloat())
                val randomGradInner = ((0..99).random().toFloat())
                percentExternal = randomGradExternal
                percentInner = randomGradInner
                gradTextView.text = "${RoundChart.getAngleByPercent(randomGradExternal)}°\n${RoundChart.getAngleByPercent(randomGradInner)}°"
            }
            firstCircle.invalidate()
        }
    }
}
