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
                val randomGrad = ((0..99).random().toFloat())
                percentExternal = randomGrad
                gradTextView.text = "${RoundChart.getAngleByPercent(randomGrad)}°"
            }
            firstCircle.invalidate()
        }
    }
}
