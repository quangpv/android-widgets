package com.android.widgets.wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.android.widgets.R;
import com.android.widgets.wheel.decorator.AtmosphericDecorator;
import com.android.widgets.wheel.decorator.CurvedDecorator;
import com.android.widgets.wheel.drawable.CurtainDrawable;
import com.android.widgets.wheel.drawable.IndicatorDrawable;
import com.android.widgets.wheel.drawable.TextColorStateListDrawable;
import com.android.widgets.wheel.drawable.TextWheelDrawable;

import java.util.Arrays;
import java.util.List;

import static com.android.widgets.wheel.drawable.TextWheelDrawable.ALIGN_CENTER;


public class WheelView extends View {
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SCROLLING = 2;
    private CurtainDrawable mCurrentItemDrawable;
    private TextWheelDrawable mTextWheelDrawable;
    private IndicatorDrawable mIndicatorDrawable;
    private WheelScroller mScroller;
    private OnItemSelectedListener mOnItemSelectedListener;
    private OnWheelChangeListener mOnWheelChangeListener;

    private List mData;
    private String mMaxWidthText;
    private int mVisibleItemCount, mDrawnItemCount;
    private int mHalfDrawnItemCount;
    private int mTextMaxWidth, mTextMaxHeight;
    private int mItemSpace;
    private int mSelectedItemPosition;
    private int mTextMaxWidthPosition;
    private int mLastPointY;
    private int mDownPointY;
    private int mTouchSlop = 8;
    private boolean hasSameWidth;
    private boolean isClick;
    private boolean isTouchTriggered;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new WheelScroller(this);
        mCurrentItemDrawable = new CurtainDrawable(this);
        mTextWheelDrawable = new TextWheelDrawable(this);
        loadAttrsIfNeeded(context, attrs);
        updateVisibleItemCount();
        computeTextSize();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            ViewConfiguration conf = ViewConfiguration.get(getContext());
            mScroller.setMinVelocity(conf.getScaledMinimumFlingVelocity());
            mScroller.setMaxVelocity(conf.getScaledMaximumFlingVelocity());
            mTouchSlop = conf.getScaledTouchSlop();
        }
        setupScroll();
    }

    private void setupScroll() {
        mScroller.setOnScrollFinishListener(new Runnable() {
            @Override
            public void run() {
                if (mData == null || mData.isEmpty()) return;
                if (mTextWheelDrawable.getItemHeight() == 0) return;
                int position = (-mScroller.getOffsetY() / mTextWheelDrawable.getItemHeight() + mSelectedItemPosition) % mData.size();
                position = position < 0 ? position + mData.size() : position;
                mCurrentItemDrawable.setPosition(position);
                if (null != mOnItemSelectedListener && isTouchTriggered)
                    mOnItemSelectedListener.onItemSelected(WheelView.this, mData.get(position), position);
                if (null != mOnWheelChangeListener && isTouchTriggered) {
                    mOnWheelChangeListener.onWheelSelected(position);
                    mOnWheelChangeListener.onWheelScrollStateChanged(SCROLL_STATE_IDLE);
                }
            }
        });
        mScroller.setOnScrollingListener(new Runnable() {
            @Override
            public void run() {
                if (null != mOnWheelChangeListener)
                    mOnWheelChangeListener.onWheelScrollStateChanged(SCROLL_STATE_SCROLLING);
            }
        });
    }

    private void loadAttrsIfNeeded(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        int idData = a.getResourceId(R.styleable.WheelView_wheelData, 0);
        mData = Arrays.asList(getResources().getStringArray(idData == 0 ? R.array.WheelArrayWeek : idData));
        mVisibleItemCount = a.getInt(R.styleable.WheelView_wheelVisibleItemCount, 7);
        mSelectedItemPosition = a.getInt(R.styleable.WheelView_wheelPositionSelected, 0);
        hasSameWidth = a.getBoolean(R.styleable.WheelView_wheelSameWidth, false);
        mTextMaxWidthPosition = a.getInt(R.styleable.WheelView_wheelMaximumWidthTextPosition, -1);
        mMaxWidthText = a.getString(R.styleable.WheelView_wheelMaximumWidthText);
        mItemSpace = a.getDimensionPixelSize(R.styleable.WheelView_wheelItemSpace, getResources().getDimensionPixelSize(R.dimen.size_of_wheel_item));
        mScroller.setCyclic(a.getBoolean(R.styleable.WheelView_wheelCyclic, false));

        if (a.getBoolean(R.styleable.WheelView_wheelIndicator, false)) {
            mIndicatorDrawable = new IndicatorDrawable(this);
            mIndicatorDrawable.setColor(a.getColor(R.styleable.WheelView_wheelIndicatorColor, 0xFFEE3333));
            mIndicatorDrawable.setIndicatorSize(a.getDimensionPixelSize(R.styleable.WheelView_wheelIndicatorSize,
                    getResources().getDimensionPixelSize(R.dimen.size_of_wheel_indicator)));
        }

        mCurrentItemDrawable.setCurtain(a.getBoolean(R.styleable.WheelView_wheelCurtain, false));
        mCurrentItemDrawable.setCurtainColor(a.getColor(R.styleable.WheelView_wheelCurtainColor, 0x88FFFFFF));
        int selectedColor = a.getColor(R.styleable.WheelView_wheelSelectedTextColor, -1);
        if (selectedColor != -1) {
            mTextWheelDrawable = new TextColorStateListDrawable(this, mCurrentItemDrawable);
            ((TextColorStateListDrawable) mTextWheelDrawable).setSelectedColor(selectedColor);
        }
        mTextWheelDrawable.setColor(a.getColor(R.styleable.WheelView_wheelTextColor, 0xFF888888));
        mTextWheelDrawable.setTextSize(a.getDimensionPixelSize(R.styleable.WheelView_wheelTextSize, getResources().getDimensionPixelSize(R.dimen.text_size_of_wheel_item)));
        mTextWheelDrawable.setAlignment(a.getInt(R.styleable.WheelView_wheelItemAlign, ALIGN_CENTER));
        mTextWheelDrawable.setFontPath(a.getString(R.styleable.WheelView_wheelFontPath));

        if (a.getBoolean(R.styleable.WheelView_wheelCurved, false)) {
            mTextWheelDrawable.addDecorator(new CurvedDecorator());
        }
        if (a.getBoolean(R.styleable.WheelView_wheelAtmospheric, false)) {
            mTextWheelDrawable.addDecorator(new AtmosphericDecorator());
        }
        a.recycle();
    }

    private void updateVisibleItemCount() {
        if (mVisibleItemCount < 2)
            throw new ArithmeticException("Wheel's visible item count can not be less than 2!");

        if (mVisibleItemCount % 2 == 0)
            mVisibleItemCount += 1;
        mDrawnItemCount = mVisibleItemCount + 2;
        mHalfDrawnItemCount = mDrawnItemCount / 2;
    }

    private void computeTextSize() {
        mTextMaxWidth = mTextMaxHeight = 0;
//        if (hasSameWidth) {
//            mTextMaxWidth = (int) mTextWheelDrawable.measureText(String.valueOf(mData.get(0)));
//        } else if (isPosInRang(mTextMaxWidthPosition)) {
//            mTextMaxWidth = (int) mTextWheelDrawable.measureText(String.valueOf(mData.get(mTextMaxWidthPosition)));
//        } else if (!TextUtils.isEmpty(mMaxWidthText)) {
//            mTextMaxWidth = (int) mTextWheelDrawable.measureText(mMaxWidthText);
//        } else {
        for (Object obj : mData) {
            String text = String.valueOf(obj);
            int width = (int) mTextWheelDrawable.measureText(text);
            mTextMaxWidth = Math.max(mTextMaxWidth, width);
        }
//        }
        Log.e(getClass().getSimpleName(), mTextMaxWidth + "");
        mTextMaxHeight = (int) mTextWheelDrawable.getMaxHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int resultWidth = mTextMaxWidth;
        int resultHeight = mTextMaxHeight * mVisibleItemCount + mItemSpace * (mVisibleItemCount - 1);

        if (mTextWheelDrawable.hasCurved()) {
            resultHeight = (int) (2 * resultHeight / Math.PI);
        }

        resultWidth += getPaddingLeft() + getPaddingRight();
        resultHeight += getPaddingTop() + getPaddingBottom();

        resultWidth = measureSize(modeWidth, sizeWidth, resultWidth);
        resultHeight = measureSize(modeHeight, sizeHeight, resultHeight);

        setMeasuredDimension(resultWidth, resultHeight);
    }

    private int measureSize(int mode, int sizeExpect, int sizeActual) {
        int realSize;
        if (mode == MeasureSpec.EXACTLY) {
            realSize = sizeExpect;
        } else {
            realSize = sizeActual;
            if (mode == MeasureSpec.AT_MOST)
                realSize = Math.min(realSize, sizeExpect);
        }
        return realSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        mCurrentItemDrawable.computeBounds();
        if (mIndicatorDrawable != null) mIndicatorDrawable.computeBounds();
        mTextWheelDrawable.computeBounds();
        mScroller.setItemHeight(mTextWheelDrawable.getItemHeight());
        mScroller.computeFlingLimitY(mSelectedItemPosition * mTextWheelDrawable.getItemHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null != mOnWheelChangeListener)
            mOnWheelChangeListener.onWheelScrolled(mScroller.getOffsetY());
        int drawnDataStartPos = -mScroller.getOffsetY() / mTextWheelDrawable.getItemHeight() - mHalfDrawnItemCount;
        int drawnOffsetPos = -mHalfDrawnItemCount;
        for (int drawnDataPos = drawnDataStartPos + mSelectedItemPosition;
             drawnDataPos < drawnDataStartPos + mSelectedItemPosition + mDrawnItemCount;
             drawnDataPos++, drawnOffsetPos++) {
            mTextWheelDrawable.setText(getText(drawnDataPos));
            mTextWheelDrawable.seek(drawnOffsetPos, mScroller.getOffsetY());
            mTextWheelDrawable.draw(canvas);
        }
        mCurrentItemDrawable.draw(canvas);
        if (mIndicatorDrawable != null) mIndicatorDrawable.draw(canvas);
    }

    private String getText(int drawnDataPos) {
        String data = "";
        if (mScroller.isCyclic()) {
            int actualPos = drawnDataPos % mData.size();
            actualPos = actualPos < 0 ? (actualPos + mData.size()) : actualPos;
            data = String.valueOf(mData.get(actualPos));
        } else {
            if (isPosInRang(drawnDataPos))
                data = String.valueOf(mData.get(drawnDataPos));
        }
        return data;
    }

    private boolean isPosInRang(int position) {
        return position >= 0 && position < mData.size();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != getParent()) getParent().requestDisallowInterceptTouchEvent(true);
                isTouchTriggered = true;
                mDownPointY = mLastPointY = (int) event.getY();

                mScroller.startTracking(event);
                mScroller.finishIfNeeded();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mDownPointY - event.getY()) < mTouchSlop) {
                    isClick = true;
                    break;
                }
                isClick = false;
                mScroller.tracking(event);
                if (null != mOnWheelChangeListener)
                    mOnWheelChangeListener.onWheelScrollStateChanged(SCROLL_STATE_DRAGGING);
                float move = event.getY() - mLastPointY;
                if (Math.abs(move) < 1) break;
                mScroller.addMove(move);
                mLastPointY = (int) event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (null != getParent()) getParent().requestDisallowInterceptTouchEvent(false);
                if (isClick && !mScroller.isForceFinish()) break;
                mScroller.tracking(event);
                mScroller.scroll();
                mScroller.stopTracking();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (null != getParent()) getParent().requestDisallowInterceptTouchEvent(false);
                mScroller.stopTracking();
                break;
        }
        return true;
    }

    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public void setVisibleItemCount(int count) {
        mVisibleItemCount = count;
        updateVisibleItemCount();
        requestLayout();
    }

    public void setCyclic(boolean isCyclic) {
        mScroller.setCyclic(isCyclic);
        mScroller.computeFlingLimitY(mSelectedItemPosition * mTextWheelDrawable.getItemHeight());
        invalidate();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public void setSelectedItemPosition(int position) {
        setSelectedItemPosition(position, true);
    }

    public void setSelectedItemPosition(int position, final boolean animated) {
        isTouchTriggered = false;
        if (animated && mScroller.isFinished()) {
            int length = getData().size();
            int itemDifference = position - mCurrentItemDrawable.getPosition();
            if (itemDifference == 0) return;
            if (mScroller.isCyclic() && Math.abs(itemDifference) > (length / 2)) {
                itemDifference += (itemDifference > 0) ? -length : length;
            }
            mScroller.scrollBy(itemDifference);
        } else {
            mScroller.abortAnimationIfNeeded();
            position = Math.min(position, mData.size() - 1);
            position = Math.max(position, 0);
            mSelectedItemPosition = position;
            mCurrentItemDrawable.setPosition(position);
            mScroller.setOffsetY(0);
            mScroller.computeFlingLimitY(mSelectedItemPosition * mTextWheelDrawable.getItemHeight());
            requestLayout();
            invalidate();
        }
    }

    public int getCurrentItemPosition() {
        return mCurrentItemDrawable.getPosition();
    }

    public List getData() {
        return mData;
    }

    public void setData(List data) {
        if (null == data)
            throw new NullPointerException("WheelPicker's data can not be null!");
        mData = data;

        if (mSelectedItemPosition > data.size() - 1 || mCurrentItemDrawable.getPosition() > data.size() - 1) {
            mCurrentItemDrawable.setPosition(data.size() - 1);
            mSelectedItemPosition = mCurrentItemDrawable.getPosition();
        } else {
            mSelectedItemPosition = mCurrentItemDrawable.getPosition();
        }
        mScroller.setOffsetY(0);
        computeTextSize();
        mScroller.computeFlingLimitY(mSelectedItemPosition * mTextWheelDrawable.getItemHeight());
        requestLayout();
        invalidate();
    }

    public void setSameWidth(boolean hasSameWidth) {
        this.hasSameWidth = hasSameWidth;
        computeTextSize();
        requestLayout();
    }

    public void setOnWheelChangeListener(OnWheelChangeListener listener) {
        mOnWheelChangeListener = listener;
    }

    public void setMaximumWidthText(String text) {
        if (null == text)
            throw new NullPointerException("Maximum width text can not be null!");
        mMaxWidthText = text;
        computeTextSize();
        requestLayout();
    }

    public void setMaximumWidthTextPosition(int position) {
        if (!isPosInRang(position))
            throw new ArrayIndexOutOfBoundsException("Maximum width text Position must in [0, " +
                    mData.size() + "), but current is " + position);
        mTextMaxWidthPosition = position;
        computeTextSize();
        requestLayout();
    }

    public void setSelectedItemTextColor(int color) {
        mCurrentItemDrawable.setColor(color);
        mCurrentItemDrawable.computeBounds();
        mCurrentItemDrawable.invalidateSelf();
    }

    public void setItemTextColor(int color) {
        mTextWheelDrawable.setColor(color);
        mTextWheelDrawable.invalidateSelf();
    }

    public void setItemTextSize(int size) {
        mTextWheelDrawable.setTextSize(size);
        computeTextSize();
        requestLayout();
    }

    public void setItemSpace(int space) {
        mItemSpace = space;
        requestLayout();
    }

    public void setIndicator(boolean hasIndicator) {
        if (!hasIndicator) return;
        mIndicatorDrawable = new IndicatorDrawable(this);
        mIndicatorDrawable.computeBounds();
        mIndicatorDrawable.invalidateSelf();
    }

    public void setIndicatorSize(int size) {
        mIndicatorDrawable.setIndicatorSize(size);
        mIndicatorDrawable.computeBounds();
        mIndicatorDrawable.invalidateSelf();
    }

    public void setIndicatorColor(int color) {
        mIndicatorDrawable.setColor(color);
        mIndicatorDrawable.invalidateSelf();
    }

    public void setCurtain(boolean hasCurtain) {
        mCurrentItemDrawable.setCurtain(hasCurtain);
        mCurrentItemDrawable.computeBounds();
        mCurrentItemDrawable.invalidateSelf();
    }

    public void setCurtainColor(int color) {
        mCurrentItemDrawable.setCurtainColor(color);
        invalidate();
    }

    public void setAtmospheric(boolean hasAtmospheric) {
        if (!hasAtmospheric) return;
        mTextWheelDrawable.addDecorator(new AtmosphericDecorator());
        invalidate();
    }

    public void setCurved(boolean isCurved) {
        if (!isCurved) return;
        mTextWheelDrawable.addDecorator(new CurvedDecorator());
        requestLayout();
    }

    public void setItemAlign(int align) {
        mTextWheelDrawable.setAlignment(align);
        mTextWheelDrawable.invalidateSelf();
    }

    public void setTypeface(Typeface tf) {
        mTextWheelDrawable.setTypeface(tf);
        computeTextSize();
        requestLayout();
    }

    public int getItemHeight() {
        return mTextWheelDrawable.getItemHeight();
    }

    public interface OnItemSelectedListener {
        void onItemSelected(WheelView view, Object data, int position);
    }

    public interface OnWheelChangeListener {
        void onWheelScrolled(int offset);

        void onWheelSelected(int position);

        void onWheelScrollStateChanged(int state);
    }
}
