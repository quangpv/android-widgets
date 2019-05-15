package com.android.widgets.wheel;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WheelDayView extends WheelView {
    private static final SparseArray<List<Integer>> DAYS = new SparseArray<>();

    private int mCurrentYear;
    private int mCurrentMonth;

    public WheelDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Calendar calendar = Calendar.getInstance();
        mCurrentYear = calendar.get(Calendar.YEAR);
        mCurrentMonth = calendar.get(Calendar.MONTH) + 1;
        setDate(mCurrentMonth, mCurrentYear);
        setSelectedItemPosition(calendar.get(Calendar.DAY_OF_MONTH) - 1, false);
    }

    private void setDate(int month, int year) {
        mCurrentMonth = month;
        mCurrentYear = year;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<Integer> data = DAYS.get(days);
        if (null == data) {
            data = new ArrayList<>();
            for (int i = 1; i <= days; i++) data.add(i);
            DAYS.put(days, data);
        }
        super.setData(data);
    }

    @Override
    public void setData(List data) {
        throw new UnsupportedOperationException("You can not invoke setData in WheelDayPicker");
    }

    public int getSelectedDay() {
        return (int) getData().get(getCurrentItemPosition());
    }

    public void setYear(int year) {
        setDate(mCurrentMonth, year);
    }

    public void setMonth(int month) {
        setDate(month, mCurrentYear);
    }
}