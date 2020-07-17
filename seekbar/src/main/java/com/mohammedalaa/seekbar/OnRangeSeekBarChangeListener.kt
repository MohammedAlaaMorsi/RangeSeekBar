package com.mohammedalaa.seekbar


interface OnRangeSeekBarChangeListener {
    fun onProgressChanged(seekBar: RangeSeekBarView?, progress: Int, fromUser: Boolean)
    fun onStartTrackingTouch(seekBar: RangeSeekBarView?,progress: Int)
    fun onStopTrackingTouch(seekBar: RangeSeekBarView?,progress: Int)
}