package com.gromyk.chart

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firstCircle.setClickListener()
        secondCircle.setClickListener()
        thirdCircle.setClickListener()
        fourthCircle.setClickListener()
        fifthCircle.setClickListener()
        sixthCircle.setClickListener()

    }
}

fun RoundChart.setClickListener() {
    setOnClickListener{
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
