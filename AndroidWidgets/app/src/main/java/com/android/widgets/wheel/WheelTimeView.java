package com.android.widgets.wheel;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;


import com.android.widgets.R;
import com.android.widgets.wheel.drawable.SelectedDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WheelTimeView extends LinearLayout {
    private SelectedDrawable mSelected = new SelectedDrawable();
    private WheelView mHourPicker;
    private WheelView mMinPicker;
    private Calendar mCalendar;

    public WheelTimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public WheelTimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_wheel_time_picker, this, true);
        mHourPicker = findViewById(R.id.wHour);
        mMinPicker = findViewById(R.id.wMin);
        mSelected.setColor(getResources().getColor(R.color.colorPrimary));
        mSelected.setIndicatorHeight(getResources().getDimensionPixelSize(R.dimen.size_of_wheel_indicator));
        mCalendar = Calendar.getInstance();

        initTimes();
    }

    private void initTimes() {
        mHourPicker.setData(getHours());
        mMinPicker.setData(getMinutes());

        mHourPicker.setSelectedItemPosition(mCalendar.get(Calendar.HOUR_OF_DAY), false);
        mMinPicker.setSelectedItemPosition(mCalendar.get(Calendar.MINUTE), false);
    }

    private List getMinutes() {
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            items.add(i);
        }
        return items;
    }

    private List getHours() {
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            items.add(i);
        }
        return items;
    }

    public int getHour() {
        return mHourPicker.getCurrentItemPosition();
    }

    public int getMinute() {
        return mMinPicker.getCurrentItemPosition();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mSelected.measure(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int middleY = (b - t) / 2;
        int itemHeight = mHourPicker.getItemHeight();
        mSelected.layout(l, middleY - itemHeight / 2, r, middleY + itemHeight / 2);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mSelected.draw(canvas);
    }
}
