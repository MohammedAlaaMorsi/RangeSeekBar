package com.mohammedalaa.rangeseekbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.mohammedalaa.seekbar.OnRangeSeekBarChangeListener

import com.mohammedalaa.seekbar.RangeSeekBarView

class MainActivity : AppCompatActivity() {

    lateinit var rangeSeekBarView: RangeSeekBarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rangeSeekBarView = findViewById(R.id.range_seekbar)
        rangeSeekBarView.setCurrentValue(40)
        rangeSeekBarView.setAnimated(true, 3000L)

        rangeSeekBarView.setOnRangeSeekBarViewChangeListener(object : OnRangeSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RangeSeekBarView?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBarView?) {

            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBarView?) {

            }

        })

    }

    fun getValue(view: View) {
        Toast.makeText(this, rangeSeekBarView.getCurrentValue().toString() + "", Toast.LENGTH_SHORT).show()

    }
}
