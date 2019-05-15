package com.android.widgets.wheel;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class WheelYearView extends WheelView {

    public WheelYearView(Context context) {
        this(context, null);
    }

    public WheelYearView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setYear(int startYear, int endYear, int selectedYear) {
        List<Integer> data = new ArrayList<>();
        for (int i = startYear; i <= endYear; i++) data.add(i);
        super.setData(data);
        setSelectedItemPosition(selectedYear - startYear, false);
    }

    @Override
    public void setData(List data) {
        throw new UnsupportedOperationException("You can not invoke setData in WheelYearPicker");
    }

    public int getSelectedYear() {
        return (int) getData().get(getCurrentItemPosition());
    }
}