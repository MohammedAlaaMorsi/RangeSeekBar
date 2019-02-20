package com.mohammedalaa.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class RangeSeekBarView extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {

    private int maxValue = 0;
    private int currentValue = 0;
    private int minValue = 0;
    private float valueToDraw;
    private int step = 0;

    private int barHeight;
    private int circleRadius;
    private int circleTextSize;
    private int circleTextColor;
    private int baseColor;
    private int fillColor;

    private Paint barBasePaint;
    private Paint barFillPaint;
    private Paint circlePaint;
    private Paint currentValuePaint;


    public RangeSeekBarView(Context context) {
        super(context);
    }

    public RangeSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setSaveEnabled(true);
        this.getThumb().mutate().setAlpha(0);
        this.setBackgroundColor(Color.TRANSPARENT);
        setBackgroundColor(Color.TRANSPARENT);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RangeSeekBarView, 0, 0);

        if (typedArray.hasValue(R.styleable.RangeSeekBarView_stepValue)) {
            step = typedArray.getInt(R.styleable.RangeSeekBarView_stepValue, 0);
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_minValue)) {
            minValue = typedArray.getInt(R.styleable.RangeSeekBarView_minValue, 0);
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_maxValue)) {
            maxValue = typedArray.getInt(R.styleable.RangeSeekBarView_maxValue, 0);
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_currentValue)) {
            currentValue = typedArray.getInt(R.styleable.RangeSeekBarView_currentValue, 0);
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_barHeight)) {
            barHeight = typedArray.getDimensionPixelSize(R.styleable.RangeSeekBarView_barHeight, 0);
        }

        if (typedArray.hasValue(R.styleable.RangeSeekBarView_circleRadius)) {
            circleRadius = typedArray.getDimensionPixelSize(R.styleable.RangeSeekBarView_circleRadius, 0);
        }

        if (typedArray.hasValue(R.styleable.RangeSeekBarView_circleTextSize)) {
            circleTextSize = typedArray.getDimensionPixelSize(R.styleable.RangeSeekBarView_circleTextSize, 0);
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_circleTextColor)) {
            circleTextColor = typedArray.getColor(R.styleable.RangeSeekBarView_circleTextColor, Color.RED);
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_baseColor)) {
            baseColor = typedArray.getColor(R.styleable.RangeSeekBarView_baseColor, Color.GRAY);
        }
        if (typedArray.hasValue(R.styleable.RangeSeekBarView_fillColor)) {
            fillColor = typedArray.getColor(R.styleable.RangeSeekBarView_fillColor, Color.BLACK);
        }
        this.setMax(100);

        if (currentValue < minValue || currentValue > maxValue) {
            throw new RuntimeException("Value must be in range   (min <= value <= max) ");
        }

        this.setProgress(calculateProgress(currentValue, minValue, maxValue));
        setValue(currentValue);
        typedArray.recycle();

        barBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barBasePaint.setColor(baseColor);

        barFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barFillPaint.setColor(fillColor);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(fillColor);

        currentValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentValuePaint.setTextSize(circleTextSize);
        currentValuePaint.setColor(circleTextColor);
        currentValuePaint.setTextAlign(Paint.Align.CENTER);
        setOnSeekBarChangeListener(this);
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate();
        requestLayout();
    }

    private int calculateProgress(int value, int MIN, int MAX) {
        return (100 * (value - MIN)) / (MAX - MIN);
    }

    public void setMinValue(int value) {
        this.minValue = value;
        invalidate();
        requestLayout();
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setValue(int newValue) {
        if (newValue < minValue || newValue > maxValue) {
            newValue = currentValue;
        }
        currentValue = newValue;
        valueToDraw = currentValue;
        invalidate();
    }

    public int getValue() {
        return currentValue;
    }


    private int measureHeight(int measureSpec) {
        int size = getPaddingTop() + getPaddingBottom();
        size += Math.max(barHeight, circleRadius * 2);
        return resolveSizeAndState(size, measureSpec, 0);
    }

    private int measureWidth(int measureSpec) {
        int size = getPaddingLeft() + getPaddingRight();
        return resolveSizeAndState(size, measureSpec, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBar(canvas);
    }


    private void drawBar(Canvas canvas) {
        float barLength = getWidth() - getPaddingRight() - getPaddingLeft();

        float barCenter = getBarCenter();

        float halfBarHeight = barHeight / 2;
        float top = barCenter - halfBarHeight;
        float bottom = barCenter + halfBarHeight;
        float left = getPaddingLeft();
        float right = getPaddingLeft() + barLength;
        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(rect, halfBarHeight, halfBarHeight, barBasePaint);


        float percentFilled = (float) getProgress() / getMax();
        float fillLength = barLength * percentFilled;
        float fillPosition = left + fillLength;
        RectF fillRect = new RectF(left, top, fillPosition, bottom);
        canvas.drawRoundRect(fillRect, halfBarHeight, halfBarHeight, barFillPaint);

        canvas.drawCircle(fillPosition, barCenter, circleRadius, circlePaint);

        Rect bounds = new Rect();
        String valueString = String.valueOf(Math.round(valueToDraw));
        currentValuePaint.getTextBounds(valueString, 0, valueString.length(), bounds);
        float y = barCenter + (bounds.height() / 2);

        canvas.drawText(valueString, fillPosition, y, currentValuePaint);
    }


    private float getBarCenter() {
        float barCenter = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
        //barCenter += getPaddingTop() + .1f * getHeight(); //move it down a bit
        return barCenter;
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.value = currentValue;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        currentValue = ss.value;
        valueToDraw = currentValue;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //int value = minValue + (progress);
        double value = Math.round((progress * (getMaxValue() - getMinValue())) / 100);
        int displayValue = (((int) value + getMinValue()) / step) * step;
        setValue(displayValue);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private static class SavedState extends BaseSavedState {
        int value;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            value = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
