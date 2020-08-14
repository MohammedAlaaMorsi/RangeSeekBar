package com.mohammedalaa.seekbar


interface OnDoubleValueSeekBarChangeListener {
    fun onValueChanged(seekBar: DoubleValueSeekBarView?, min: Int, max: Int, fromUser: Boolean)
    fun onStartTrackingTouch(seekBar: DoubleValueSeekBarView?, min: Int, max: Int)
    fun onStopTrackingTouch(seekBar: DoubleValueSeekBarView?, min: Int, max: Int)
}