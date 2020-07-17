package com.mohammedalaa.seekbar

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.roundToInt


@SuppressLint("AppCompatCustomView")
class RangeSeekBarView : View {


    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }


    companion object {
        private const val DEFAULT_BASE_COLOR = Color.GRAY
        private const val DEFAULT_FILL_COLOR = Color.RED
        private const val DEFAULT_TEXT_COLOR = Color.DKGRAY
        private const val DEFAULT_CIRCLE_COLOR = Color.GREEN
        private const val DEFAULT_VALUE = 0
        private const val DEFAULT_MAX_VALUE = 100
        private const val CLICK_ACTION_THRESHOLD = 5
    }


    private var minValue: Int = 0
    private var valueToDraw: Float = 0f
    private var step = 0

    private var barHeight: Int = 0
    private var circleRadius: Int = 0
    private var circleTextSize: Int = 0
    private var progress: Int = 0
    private var defaultPadding: Int = 15


    private var barBasePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var barFillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var circlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var valuePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    private var direction: Direction = Direction.LEFT_TO_RIGHT
    private var animated: Boolean = false
    private var animationDuration = 3000L
    private var animation: ValueAnimator? = null
    private var mOnRangeSeekBarViewChangeListener: OnRangeSeekBarChangeListener? = null

    private val barCenter: Float
        get() = ((height - paddingTop - paddingBottom) / 2).toFloat()


    var maxValue: Int = DEFAULT_MAX_VALUE
        set(value: Int) {
            field = value
            invalidate()
            requestLayout()
        }
    var baseColor: Int = DEFAULT_BASE_COLOR
        set(@ColorInt color) {
            field = color
            barBasePaint.color = color
            invalidate()
        }
    var fillColor: Int = DEFAULT_FILL_COLOR
        set(@ColorInt color) {
            field = color
            barFillPaint.color = color
            invalidate()
        }
    var circleTextColor: Int = DEFAULT_TEXT_COLOR
        set(@ColorInt color) {
            field = color
            valuePaint.color = color
            invalidate()
        }

    var circleFillColor: Int = DEFAULT_CIRCLE_COLOR
        set(@ColorInt color) {
            field = color
            circlePaint.color = color
            invalidate()
        }

    private var isTouchListenerEnabled = true


    var currentValue: Int = DEFAULT_VALUE
        set(value) {
            val previousValue = currentValue
            field = value
            var newValue = value

            if (newValue < minValue || newValue > maxValue) {
                newValue = currentValue
            }
            if (newValue % step == 0) {
                field = newValue
            }

            mOnRangeSeekBarViewChangeListener?.onProgressChanged(this@RangeSeekBarView, newValue, true);

            animation?.cancel()

            if (animated) {
                animation = ValueAnimator.ofFloat(previousValue.toFloat(), currentValue.toFloat())
                val changeInValue = Math.abs(currentValue - previousValue)
                val durationToUse = (animationDuration * (changeInValue.toFloat() / maxValue.toFloat())).toLong()
                animation?.duration = durationToUse

                animation?.addUpdateListener { valueAnimator ->
                    valueToDraw = valueAnimator.animatedValue as Float
                    mOnRangeSeekBarViewChangeListener?.onProgressChanged(this@RangeSeekBarView, valueToDraw.toInt(), true)
                    this.invalidate()
                }

                animation!!.start()
            } else {
                valueToDraw = currentValue.toFloat()
            }
            invalidate()

        }

    private fun init(attrs: AttributeSet) {
        parseAttr(attrs)
        barBasePaint.apply {
            color = baseColor
        }
        barFillPaint.apply {
            color = fillColor
        }
        valuePaint.apply {
            textSize = circleTextSize.toFloat()
            color = circleTextColor
            textAlign = Paint.Align.CENTER
        }
        circlePaint.apply {
            color = circleFillColor
        }
        setValues()

    }


    private fun setValues() {
        isSaveEnabled = true
        setBackgroundColor(Color.TRANSPARENT)
        if (currentValue < minValue || currentValue > maxValue) {
            throw RuntimeException("Value must be in range   (min <= value <= max) ")
        }
        progress = calculateProgress(currentValue, minValue, maxValue)
    }

    private fun parseAttr(attrs: AttributeSet?) {
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
            defaultPadding = circleRadius
        }

        if (typedArray.hasValue(R.styleable.RangeSeekBarView_circleTextSize)) {
            circleTextSize = typedArray.getDimensionPixelSize(R.styleable.RangeSeekBarView_circleTextSize, 0)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_circleTextColor)) {
            circleTextColor = typedArray.getColor(R.styleable.RangeSeekBarView_circleTextColor, DEFAULT_TEXT_COLOR)
        }

        if (typedArray.hasValue(R.styleable.RangeSeekBarView_circleFillColor)) {
            circleFillColor = typedArray.getColor(R.styleable.RangeSeekBarView_circleFillColor, DEFAULT_CIRCLE_COLOR)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_baseColor)) {
            baseColor = typedArray.getColor(R.styleable.RangeSeekBarView_baseColor, DEFAULT_BASE_COLOR)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_fillColor)) {
            fillColor = typedArray.getColor(R.styleable.RangeSeekBarView_fillColor, DEFAULT_FILL_COLOR)
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_orientation)) {
            direction = Direction.values()[typedArray.getInt(R.styleable.RangeSeekBarView_orientation, 1)]
        }
        setPadding(defaultPadding, 0, defaultPadding, 0)

        typedArray.recycle()

    }


    fun setOnRangeSeekBarViewChangeListener(l: OnRangeSeekBarChangeListener) {
        mOnRangeSeekBarViewChangeListener = l
    }

    private fun calculateProgress(value: Int, MIN: Int, MAX: Int): Int {
        return 100 * (value - MIN) / (MAX - MIN)
    }

    fun setMinValue(value: Int) {
        minValue = value
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
        return resolveSizeAndState(size, measureSpec, 0)
    }

    private fun measureWidth(measureSpec: Int): Int {
        val size = paddingLeft + paddingRight
        return resolveSizeAndState(size, measureSpec, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        when (direction) {
            Direction.TOP_TO_BOTTOM -> {
                super.onDraw(canvas)
                drawBar(canvas)

            }
            Direction.LEFT_TO_RIGHT -> {

            }
            Direction.RIGHT_TO_LEFT -> {

            }
            Direction.BOTTOM_TO_TOP -> {

            }
        }


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
        canvas.drawRoundRect(rect, halfBarHeight, halfBarHeight, barBasePaint)


        val percentFilled = calculateProgress(valueToDraw.toInt(), minValue, maxValue).toFloat() / DEFAULT_MAX_VALUE
        val fillLength = barLength * percentFilled
        val fillPosition = left + fillLength
        val fillRect = RectF(left, top, fillPosition, bottom)
        canvas.drawRoundRect(fillRect, halfBarHeight, halfBarHeight, barFillPaint)

        canvas.drawCircle(fillPosition, barCenter, circleRadius.toFloat(), circlePaint)

        val bounds = Rect()
        val valueString = Math.round(valueToDraw).toString()
        valuePaint.getTextBounds(valueString, 0, valueString.length, bounds)
        val y = barCenter + bounds.height() / 2

        canvas.drawText(valueString, fillPosition, y, valuePaint)


    }


    private fun upDatePosition(value: Int) {
        val calcValue = (value * (maxValue - minValue) / 100).toFloat().roundToInt().toDouble()
        val displayValue = (calcValue.toInt() + minValue) / step * step
        currentValue = displayValue
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


    override fun performClick(): Boolean {
        super.performClick()
        return true // register it has been handled
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isTouchListenerEnabled) {
            return false
        }
        val eventAction = event.action

        // you may need the x/y location
        var x = event.x.toDouble()
        val canvas_width = (width - paddingStart - paddingEnd).toDouble()

        if (x < 0) {
            x = 0.0
        } else if (x > canvas_width) {
            x = canvas_width
        }
        when (eventAction) {
            MotionEvent.ACTION_DOWN -> {
                //isUserTouched = true
                mOnRangeSeekBarViewChangeListener?.onStartTrackingTouch(this@RangeSeekBarView, currentValue)

            }
            MotionEvent.ACTION_UP -> {
                //isUserTouched = false
                val value = (x / canvas_width * 100).toInt()
                upDatePosition(value)
                mOnRangeSeekBarViewChangeListener?.onStopTrackingTouch(this@RangeSeekBarView, currentValue)

            }
            MotionEvent.ACTION_MOVE -> {
                val value = (x / canvas_width * 100).toInt()
                upDatePosition(value)
                mOnRangeSeekBarViewChangeListener?.onProgressChanged(this@RangeSeekBarView, currentValue, true)

            }
        }
        return true
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
