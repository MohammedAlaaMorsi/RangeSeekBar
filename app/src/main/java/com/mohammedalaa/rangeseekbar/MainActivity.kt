package com.mohammedalaa.rangeseekbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mohammedalaa.seekbar.DoubleValueSeekBarView
import com.mohammedalaa.seekbar.OnDoubleValueSeekBarChangeListener
import com.mohammedalaa.seekbar.OnRangeSeekBarChangeListener

import com.mohammedalaa.seekbar.RangeSeekBarView

class MainActivity : AppCompatActivity() {

    lateinit var rangeSeekBarView1: RangeSeekBarView
    lateinit var rangeSeekBarView2: RangeSeekBarView
    lateinit var rangeSeekBarView3: RangeSeekBarView
    lateinit var rangeSeekBarView4: RangeSeekBarView

    lateinit var doubleValueSeekBarView: DoubleValueSeekBarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rangeSeekBarView1 = findViewById<RangeSeekBarView>(R.id.range_seekbar1)
        rangeSeekBarView2 = findViewById(R.id.range_seekbar2)
        rangeSeekBarView3 = findViewById(R.id.range_seekbar3)
        rangeSeekBarView4 = findViewById(R.id.range_seekbar4)

        doubleValueSeekBarView = findViewById(R.id.range_seekbar)



        rangeSeekBarView1.currentValue=20
        rangeSeekBarView2.currentValue=30
        rangeSeekBarView3.currentValue=40
        rangeSeekBarView4.currentValue=50

        doubleValueSeekBarView.currentMinValue=50
        doubleValueSeekBarView.currentMaxValue=140


        rangeSeekBarView1.setAnimated(true, 3000L)
        rangeSeekBarView2.setAnimated(true, 3000L)
        rangeSeekBarView3.setAnimated(true, 3000L)
        rangeSeekBarView4.setAnimated(true, 3000L)

        rangeSeekBarView1.setOnRangeSeekBarViewChangeListener(object : OnRangeSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RangeSeekBarView?, progress: Int, fromUser: Boolean) {
                Log.e("onChanged:1->",progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBarView?,progress:Int) {
                Log.e("onStart:1->",progress.toString())

            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBarView?,progress:Int) {
                Log.e("onStop:1->",progress.toString())

            }

        })


        rangeSeekBarView2.setOnRangeSeekBarViewChangeListener(object : OnRangeSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RangeSeekBarView?, progress: Int, fromUser: Boolean) {
                Log.e("onChanged:2->",progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBarView?,progress:Int) {
                Log.e("onStart:2->",progress.toString())

            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBarView?,progress:Int) {
                Log.e("onStop:2->",progress.toString())

            }

        })

        rangeSeekBarView3.setOnRangeSeekBarViewChangeListener(object : OnRangeSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RangeSeekBarView?, progress: Int, fromUser: Boolean) {
                Log.e("onChanged:3->",progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBarView?,progress:Int) {
                Log.e("onStart:3->",progress.toString())

            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBarView?,progress:Int) {
                Log.e("onStop:3->",progress.toString())

            }

        })


        rangeSeekBarView4.setOnRangeSeekBarViewChangeListener(object : OnRangeSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RangeSeekBarView?, progress: Int, fromUser: Boolean) {
                Log.e("onChanged:4->",progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBarView?,progress:Int) {
                Log.e("onStart:4->",progress.toString())

            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBarView?,progress:Int) {
                Log.e("onStop:4->",progress.toString())

            }

        })

        doubleValueSeekBarView.setOnRangeSeekBarViewChangeListener(object : OnDoubleValueSeekBarChangeListener {
            override fun onValueChanged(seekBar: DoubleValueSeekBarView?, min: Int, max: Int, fromUser: Boolean) {
                Log.e("onChanged->","Min $min : Max $max")

            }

            override fun onStartTrackingTouch(seekBar: DoubleValueSeekBarView?, min: Int, max: Int) {
                Log.e("onStart->","Min $min : Max $max")
            }

            override fun onStopTrackingTouch(seekBar: DoubleValueSeekBarView?, min: Int, max: Int) {
                Log.e("onStop->","Min $min : Max $max")
            }

        })

    }

    fun getValues(view: View) {
        Toast.makeText(this,
                "1->${rangeSeekBarView1.currentValue}\n" +
                "2->${rangeSeekBarView2.currentValue}\n" +
                "3->${rangeSeekBarView3.currentValue}\n" +
                "4->${rangeSeekBarView4.currentValue}\n" +
                "min->${doubleValueSeekBarView.currentMinValue} max->${doubleValueSeekBarView.currentMaxValue}"
                ,Toast.LENGTH_SHORT).show()
    }

    /* fun getValue(view: View) {
         Toast.makeText(this, rangeSeekBarView.currentValue.toString() + "", Toast.LENGTH_SHORT).show()

     }*/
}
