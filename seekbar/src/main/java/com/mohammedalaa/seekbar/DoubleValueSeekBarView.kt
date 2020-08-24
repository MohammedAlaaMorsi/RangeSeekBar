package com.mohammedalaa.seekbar

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.roundToInt


@SuppressLint("AppCompatCustomView")
open class DoubleValueSeekBarView : View {


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
        private const val DEFAULT_MIN_VALUE = 0
        private const val DEFAULT_MAX_VALUE = 100
        private const val DEFAULT_MAX_STEP_VALUE = 1
        private const val DEFAULT_MIN_STEP_VALUE = 1

    }

    private var seekBarTouchEnabled = true
    private var pressedThumb: Thumb? = null
    private var mActivePointerId = INVALID_POINTER_ID
    private var pointerIndex = 0
    private var isDragging = true
    private var minValueToDraw: Int = 0
    private var maxValueToDraw: Int = 100


    private var barHeight: Int = 0
    private var circleRadius: Int = 0
    private var circleTextSize: Int = 0
    private var defaultPadding: Int = 15

    private var barBasePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var barFillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var minCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var maxCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var minValuePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var maxValuePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mOnDoubleValueSeekBarViewChangeListener: OnDoubleValueSeekBarChangeListener? = null



    var minStep: Int = DEFAULT_MIN_STEP_VALUE
        set(value: Int) {
            field = value
            invalidate()
            requestLayout()
        }
    var maxStep: Int = DEFAULT_MAX_STEP_VALUE
        set(value: Int) {
            field = value
            invalidate()
            requestLayout()
        }
    var maxValue: Int = DEFAULT_MAX_VALUE
        set(value: Int) {
            field = value
            invalidate()
            requestLayout()
        }

    var minValue: Int = DEFAULT_MIN_VALUE
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
            minValuePaint.color = color
            maxValuePaint.color = color
            invalidate()
        }

    var circleFillColor: Int = DEFAULT_CIRCLE_COLOR
        set(@ColorInt color) {
            field = color
            minCirclePaint.color = color
            maxCirclePaint.color = color
            invalidate()
        }

    var currentMinValue: Int = DEFAULT_MIN_VALUE
        set(value) {
            if (value > currentMaxValue) {
                return
            }
            field = value
            var newValue = value

            if (newValue < minValue || newValue > maxValue) {
                newValue = currentMinValue
            }
            if (newValue % maxStep == 0) {
                field = newValue
            }
            minValueToDraw = currentMinValue
            invalidate()


        }

    var currentMaxValue: Int = DEFAULT_MAX_VALUE
        set(value) {
            if (value < currentMinValue) {
                return
            }
            field = value
            var newValue = value

            if (newValue < minValue || newValue > maxValue) {
                newValue = currentMaxValue
            }
            if (newValue % maxStep == 0) {
                field = newValue
            }
            maxValueToDraw = currentMaxValue
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
        minValuePaint.apply {
            textSize = circleTextSize.toFloat()
            color = circleTextColor
            textAlign = Paint.Align.CENTER
        }
        maxValuePaint.apply {
            textSize = circleTextSize.toFloat()
            color = circleTextColor
            textAlign = Paint.Align.CENTER
        }
        minCirclePaint.apply {
            color = circleFillColor
        }
        maxCirclePaint.apply {
            color = circleFillColor
        }

    }


    private fun parseAttr(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DoubleValueSeekBarView, 0, 0)
        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2minValueStep)) {
            minStep = typedArray.getInt(R.styleable.DoubleValueSeekBarView_r2minValueStep, 0)
        }
        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2maxValueStep)) {
            maxStep = typedArray.getInt(R.styleable.DoubleValueSeekBarView_r2maxValueStep, 0)
        }

        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2minValue)) {
            minValue = typedArray.getInt(R.styleable.DoubleValueSeekBarView_r2minValue, 0)
        }
        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2maxValue)) {
            maxValue = typedArray.getInt(R.styleable.DoubleValueSeekBarView_r2maxValue, 0)
        }
        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2barHeight)) {
            barHeight = typedArray.getDimensionPixelSize(R.styleable.DoubleValueSeekBarView_r2barHeight, 0)
        }

        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2circleRadius)) {
            circleRadius = typedArray.getDimensionPixelSize(R.styleable.DoubleValueSeekBarView_r2circleRadius, 0)
            defaultPadding = circleRadius
        }

        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2circleTextSize)) {
            circleTextSize = typedArray.getDimensionPixelSize(R.styleable.DoubleValueSeekBarView_r2circleTextSize, 0)
        }
        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2circleTextColor)) {
            circleTextColor = typedArray.getColor(R.styleable.DoubleValueSeekBarView_r2circleTextColor, DEFAULT_TEXT_COLOR)
        }

        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2circleFillColor)) {
            circleFillColor = typedArray.getColor(R.styleable.DoubleValueSeekBarView_r2circleFillColor, DEFAULT_CIRCLE_COLOR)
        }
        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2baseColor)) {
            baseColor = typedArray.getColor(R.styleable.DoubleValueSeekBarView_r2baseColor, DEFAULT_BASE_COLOR)
        }
        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2fillColor)) {
            fillColor = typedArray.getColor(R.styleable.DoubleValueSeekBarView_r2fillColor, DEFAULT_FILL_COLOR)
        }

        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2CurrentMaxValue)) {
            currentMaxValue = typedArray.getInt(R.styleable.DoubleValueSeekBarView_r2CurrentMaxValue, 100)
        }

        if (typedArray.hasValue(R.styleable.DoubleValueSeekBarView_r2CurrentMinValue)) {
            currentMinValue = typedArray.getInt(R.styleable.DoubleValueSeekBarView_r2CurrentMinValue, 0)
        }

        setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding)
        typedArray.recycle()

    }


    fun setOnRangeSeekBarViewChangeListener(l: OnDoubleValueSeekBarChangeListener) {
        mOnDoubleValueSeekBarViewChangeListener = l
    }

    private fun calculateProgress(value: Int, MIN: Int, MAX: Int): Int {
        return 100 * (value - MIN) / (MAX - MIN)
    }

    private fun measureHeight(measureSpec: Int): Int {
        var size = paddingTop + paddingBottom
        size += Math.max(barHeight, circleRadius)
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
        super.onDraw(canvas)
        drawDoubleValueBarHorizontal(canvas)
    }

    private fun drawDoubleValueBarHorizontal(canvas: Canvas) {
        val barLength = (width - paddingRight - paddingLeft).toFloat()
        val barCenter = (height / 2).toFloat()

        val halfBarHeight = (barHeight / 2).toFloat()
        val top = barCenter - halfBarHeight
        val bottom = barCenter + halfBarHeight
        val left = paddingLeft.toFloat()
        val right = paddingLeft + barLength
        val rect = RectF(left, top, right, bottom)
        canvas.drawRoundRect(rect, halfBarHeight, halfBarHeight, barBasePaint)

        val percentFilledMin = calculateProgress(minValueToDraw, minValue, maxValue).toFloat() / DEFAULT_MAX_VALUE
        val fillLengthMin = barLength * percentFilledMin
        val fillPositionMin = left + fillLengthMin


        val percentFilledMax = calculateProgress(maxValueToDraw, minValue, maxValue).toFloat() / DEFAULT_MAX_VALUE
        val fillLengthMax = barLength * percentFilledMax
        val fillPositionMax = left + fillLengthMax


        val fillRect = RectF(fillPositionMin, top, fillPositionMax, bottom)

        canvas.drawRoundRect(fillRect, barCenter, barCenter, barFillPaint)


        canvas.drawCircle(fillPositionMin, barCenter, circleRadius.toFloat(), minCirclePaint)
        canvas.drawCircle(fillPositionMax, barCenter, circleRadius.toFloat(), maxCirclePaint)

        val boundsMin = Rect()
        val minValueString = Math.round(minValueToDraw.toDouble()).toString()
        minValuePaint.getTextBounds(minValueString, 0, minValueString.length, boundsMin)

        val boundsMax = Rect()
        val maxValueString = Math.round(maxValueToDraw.toDouble()).toString()
        maxValuePaint.getTextBounds(maxValueString, 0, maxValueString.length, boundsMax)

        val y = barCenter + boundsMin.height() / 2

        canvas.drawText(minValueString, fillPositionMin.toFloat(), y, minValuePaint)
        canvas.drawText(maxValueString, fillPositionMax.toFloat(), y, maxValuePaint)

    }


    private fun upDatePositionMax(value: Int) {
        val calcValue = (value * (maxValue - minValue) / 100).toFloat().roundToInt().toDouble()
        val displayValue = ((calcValue.toInt() + minValue) / maxStep * maxStep)
        currentMaxValue = displayValue
    }

    private fun upDatePositionMin(value: Int) {
        val calcValue = (value * (maxValue - minValue) / 100).toFloat().roundToInt().toDouble()
        val displayValue = ((calcValue.toInt() + minValue) / minStep * minStep)
        currentMinValue = displayValue
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun normalizedToScreen(normalizedCoord: Int): Int {
        val widthSize: Float = ((width - paddingStart - paddingEnd)).toFloat()
        return (normalizedCoord.toFloat() / maxValue * widthSize).roundToInt()
    }

    private fun isInThumbRange(touchX: Float, normalizedThumbValue: Int): Boolean {
        return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= getThumbWidth()
    }

    private fun getThumbWidth(): Float {
        return circleRadius * 2f
    }


    private fun evalPressedThumb(touchX: Float): Thumb {
        var result: Thumb? = null
        val minThumbPressed = isInThumbRange(touchX, normalizedToScreen(currentMinValue))
        val maxThumbPressed = isInThumbRange(touchX, normalizedToScreen(currentMaxValue))
        if (minThumbPressed && maxThumbPressed) {
            result = if (touchX / width > 0.5f) Thumb.MIN else Thumb.MAX
        } else if (minThumbPressed) {
            result = Thumb.MIN
        } else if (maxThumbPressed) {
            result = Thumb.MAX
        }
        if (seekBarTouchEnabled && result == null) {
            result = findClosestThumb(touchX)
        }
        return result!!
    }


    private fun findClosestThumb(touchX: Float): Thumb? {
        val screenMinX = normalizedToScreen(currentMinValue)
        val screenMaxX = normalizedToScreen(currentMaxValue)
        if (touchX >= screenMaxX) {
            return Thumb.MAX
        } else if (touchX <= screenMinX) {
            return Thumb.MIN
        }
        val minDiff = Math.abs(screenMinX - touchX.toDouble())
        val maxDiff = Math.abs(screenMaxX - touchX.toDouble())
        return if (minDiff < maxDiff) Thumb.MIN else Thumb.MAX
    }

    private fun attemptClaimDrag() {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
    }

    private fun onStartTrackingTouch() {
        isDragging = true
    }


    private fun onStopTrackingTouch() {
        isDragging = false
    }


    private fun trackTouchEvent(event: MotionEvent) {
        val pointerIndex = event.findPointerIndex(mActivePointerId)
        try {
            val x = event.getX(pointerIndex)
            if (Thumb.MIN == pressedThumb) {
                screenToNormalized(x, Thumb.MIN)
            } else if (Thumb.MAX == pressedThumb) {
                screenToNormalized(x, Thumb.MAX)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun normalize(screenCoord: Float): Int {
        val width = width.toDouble()
        var coordinate: Float = screenCoord
        val canvasSize: Double = (width - paddingStart - paddingEnd).toDouble()
        if (coordinate < 0) {
            coordinate = 0.0F
        } else if (screenCoord > canvasSize) {
            coordinate = canvasSize.toFloat()
        }
        val value = (coordinate / canvasSize * 100).toInt()
        return value
    }

    private fun screenToNormalized(screenCoord: Float, thumb: Thumb) {
        val width = width.toDouble()
        var coordinate: Float = screenCoord
        val canvasSize: Double = (width - paddingStart - paddingEnd).toDouble()
        if (coordinate < 0) {
            coordinate = 0.0F
        } else if (screenCoord > canvasSize) {
            coordinate = canvasSize.toFloat()
        }
        val value = (coordinate / canvasSize * 100).toInt()

        if (thumb == Thumb.MIN) {
            upDatePositionMin(value)
        } else {
            upDatePositionMax(value)
        }
    }

    protected enum class Thumb {
        MIN, MAX
    }

    protected fun touchDown(x: Float, y: Float) {}

    protected fun touchMove(x: Float, y: Float) {}

    protected fun touchUp(x: Float, y: Float) {}

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        val action = event.action

        when (action and ACTION_MASK) {
            ACTION_DOWN -> {
                mActivePointerId = event.getPointerId(event.pointerCount - 1)
                pointerIndex = event.findPointerIndex(mActivePointerId)
                val mDownMotionX = event.getX(pointerIndex)
                pressedThumb = evalPressedThumb(mDownMotionX)
                if (pressedThumb == null) {
                    return super.onTouchEvent(event)
                }
                touchDown(event.getX(pointerIndex), event.getY(pointerIndex))
                isPressed = true
                invalidate()
                onStartTrackingTouch()
                trackTouchEvent(event)
                attemptClaimDrag()
            }
            ACTION_MOVE -> {
                if (pressedThumb != null) {
                    if (isDragging) {
                        touchMove(event.getX(pointerIndex), event.getY(pointerIndex))
                        trackTouchEvent(event)
                    }
                    mOnDoubleValueSeekBarViewChangeListener?.onValueChanged(this@DoubleValueSeekBarView, currentMinValue, currentMaxValue, true)

                }
            }
            ACTION_UP -> {
                if (isDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                    touchUp(event.getX(pointerIndex), event.getY(pointerIndex))
                    mOnDoubleValueSeekBarViewChangeListener?.onStopTrackingTouch(this@DoubleValueSeekBarView, currentMinValue, currentMaxValue)
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should be interpreted as a tap-seek to that location.
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }
                pressedThumb = null
                invalidate()
                mOnDoubleValueSeekBarViewChangeListener?.onValueChanged(this@DoubleValueSeekBarView, currentMinValue, currentMaxValue, true)

            }
            ACTION_POINTER_DOWN -> {
                //invalidate()
            }
            ACTION_POINTER_UP -> {
                invalidate()
            }
            ACTION_CANCEL -> {
                if (isDragging) {
                    onStopTrackingTouch()
                    isPressed = false
                    touchUp(event.getX(pointerIndex), event.getY(pointerIndex))
                }
                invalidate()
            }
        }

        return true

    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState!!)
        ss.minValue = currentMinValue
        ss.maxValue = currentMaxValue
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        currentMinValue = ss.minValue
        currentMaxValue = ss.maxValue

        minValueToDraw = currentMinValue
        maxValueToDraw = currentMaxValue
        super.onRestoreInstanceState(ss.superState)
    }

    private class SavedState : BaseSavedState {
        internal var minValue: Int = 0
        internal var maxValue: Int = 0

        internal constructor(superState: Parcelable) : super(superState) {}

        private constructor(`in`: Parcel) : super(`in`) {
            minValue = `in`.readInt()
            maxValue = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(minValue)
            out.writeInt(maxValue)
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
