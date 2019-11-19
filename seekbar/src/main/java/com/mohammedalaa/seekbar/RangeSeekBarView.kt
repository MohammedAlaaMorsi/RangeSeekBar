package com.mohammedalaa.seekbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import androidx.appcompat.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import kotlin.math.roundToInt


class RangeSeekBarView : AppCompatSeekBar, SeekBar.OnSeekBarChangeListener {

    private var maxValue = 0
    private var currentValue: Int = 0;


    private var minValue = 0
    private var valueToDraw: Float = 0.toFloat()
    private var step = 0

    private var barHeight: Int = 0
    private var circleRadius: Int = 0
    private var circleTextSize: Int = 0
    private var circleTextColor: Int = 0
    private var baseColor: Int = 0
    private var fillColor: Int = 0

    private var barBasePaint: Paint? = null
    private var barFillPaint: Paint? = null
    private var circlePaint: Paint? = null
    private var currentValuePaint: Paint? = null

    private var animated: Boolean = false
    private var animationDuration = 3000L
    private var animation: ValueAnimator? = null

    private val barCenter: Float
        get() = ((height - paddingTop - paddingBottom) / 2).toFloat()


    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet) {
        isSaveEnabled = true
        this.thumb.mutate().alpha = 0
        this.setBackgroundColor(Color.TRANSPARENT)
        setBackgroundColor(Color.TRANSPARENT)
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.RangeSeekBarView, 0, 0)

        if (typedArray.hasValue(R.styleable.RangeSeekBarView_stepValue)) {
            step = typedArray.getInt(R.styleable.RangeSeekBarView_stepValue, 0)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_minValue)) {
            minValue = typedArray.getInt(R.styleable.RangeSeekBarView_minValue, 0)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_maxValue)) {
            maxValue = typedArray.getInt(R.styleable.RangeSeekBarView_maxValue, 0)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_currentValue)) {
            currentValue = typedArray.getInt(R.styleable.RangeSeekBarView_currentValue, 0)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_barHeight)) {
            barHeight = typedArray.getDimensionPixelSize(R.styleable.RangeSeekBarView_barHeight, 0)
        }

        if (typedArray.hasValue(R.styleable.RangeSeekBarView_circleRadius)) {
            circleRadius = typedArray.getDimensionPixelSize(R.styleable.RangeSeekBarView_circleRadius, 0)
        }

        if (typedArray.hasValue(R.styleable.RangeSeekBarView_circleTextSize)) {
            circleTextSize = typedArray.getDimensionPixelSize(R.styleable.RangeSeekBarView_circleTextSize, 0)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_circleTextColor)) {
            circleTextColor = typedArray.getColor(R.styleable.RangeSeekBarView_circleTextColor, Color.RED)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_baseColor)) {
            baseColor = typedArray.getColor(R.styleable.RangeSeekBarView_baseColor, Color.GRAY)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_fillColor)) {
            fillColor = typedArray.getColor(R.styleable.RangeSeekBarView_fillColor, Color.BLACK)
        }
        this.max = 100

        if (currentValue < minValue || currentValue > maxValue) {
            throw RuntimeException("Value must be in range   (min <= value <= max) ")
        }

        this.progress = calculateProgress(currentValue, minValue, maxValue)
        setCurrentValue(currentValue)
        typedArray.recycle()

        barBasePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        barBasePaint!!.color = baseColor

        barFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        barFillPaint!!.color = fillColor

        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        circlePaint!!.color = fillColor

        currentValuePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        currentValuePaint!!.textSize = circleTextSize.toFloat()
        currentValuePaint!!.color = circleTextColor
        currentValuePaint!!.textAlign = Paint.Align.CENTER
        setOnSeekBarChangeListener(this)
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
        invalidate()
        requestLayout()
    }

    fun getCurrentValue(): Int = currentValue;
    fun setCurrentValue(newVal: Int) {
        var newValue = newVal
        val previousValue = currentValue

        if (newValue < minValue || newValue > maxValue) {
            newValue = currentValue
        }
        if (newValue % step == 0) {
            currentValue = newValue
        }

        animation?.cancel()

        if (animated) {
            animation = ValueAnimator.ofFloat(previousValue.toFloat(), currentValue.toFloat())
            val changeInValue = Math.abs(currentValue - previousValue)
            val durationToUse = (animationDuration * (changeInValue.toFloat() / maxValue.toFloat())).toLong()
            animation!!.duration = durationToUse

            animation!!.addUpdateListener { valueAnimator ->
                valueToDraw = valueAnimator.animatedValue as Float
                this@RangeSeekBarView.invalidate()
            }

            animation!!.start()
        } else {
            valueToDraw = currentValue.toFloat()
        }
        invalidate()
    }

    private fun calculateProgress(value: Int, MIN: Int, MAX: Int): Int {
        return 100 * (value - MIN) / (MAX - MIN)
    }

    fun setMinValue(value: Int) {
        this.minValue = value
        invalidate()
        requestLayout()
    }


    fun setAnimated(animated: Boolean, animationDuration: Long) {
        this.animated = animated
        this.animationDuration = animationDuration
    }


    private fun measureHeight(measureSpec: Int): Int {
        var size = paddingTop + paddingBottom
        size += Math.max(barHeight, circleRadius * 2)
        return View.resolveSizeAndState(size, measureSpec, 0)
    }

    private fun measureWidth(measureSpec: Int): Int {
        val size = paddingLeft + paddingRight
        return View.resolveSizeAndState(size, measureSpec, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        drawBar(canvas)
    }


    private fun drawBar(canvas: Canvas) {
        val barLength = (width - paddingRight - paddingLeft).toFloat()

        val barCenter = barCenter

        val halfBarHeight = (barHeight / 2).toFloat()
        val top = barCenter - halfBarHeight
        val bottom = barCenter + halfBarHeight
        val left = paddingLeft.toFloat()
        val right = paddingLeft + barLength
        val rect = RectF(left, top, right, bottom)
        canvas.drawRoundRect(rect, halfBarHeight, halfBarHeight, barBasePaint!!)


        val percentFilled = calculateProgress(valueToDraw.toInt(), minValue, maxValue).toFloat() / max
        val fillLength = barLength * percentFilled
        val fillPosition = left + fillLength
        val fillRect = RectF(left, top, fillPosition, bottom)
        canvas.drawRoundRect(fillRect, halfBarHeight, halfBarHeight, barFillPaint!!)

        canvas.drawCircle(fillPosition, barCenter, circleRadius.toFloat(), circlePaint!!)

        val bounds = Rect()
        val valueString = Math.round(valueToDraw).toString()
        currentValuePaint!!.getTextBounds(valueString, 0, valueString.length, bounds)
        val y = barCenter + bounds.height() / 2

        canvas.drawText(valueString, fillPosition, y, currentValuePaint!!)
    }


    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState!!)
        ss.value = currentValue
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        currentValue = ss.value
        valueToDraw = currentValue.toFloat()
        super.onRestoreInstanceState(ss.superState)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val calcValue = (progress * (maxValue - minValue) / 100).toFloat().roundToInt().toDouble()
        val displayValue = (calcValue.toInt() + minValue) / step * step
        setCurrentValue(displayValue)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }

    private class SavedState : BaseSavedState {
        internal var value: Int = 0

        internal constructor(superState: Parcelable) : super(superState) {}

        private constructor(`in`: Parcel) : super(`in`) {
            value = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(value)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}
