package com.android.widgets.wheel;

import android.content.Context;
import android.util.AttributeSet;


import com.android.widgets.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class WheelMonthView extends WheelView {

    public WheelMonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMonths();
    }

    private void initMonths() {
        super.setData(Arrays.asList(getResources().getStringArray(R.array.array_month)));
        setSelectedItemPosition(Calendar.getInstance().get(Calendar.MONTH), false);
    }

    @Override
    public void setData(List data) {
        throw new UnsupportedOperationException("You can not invoke setData in WheelMonthPicker");
    }

    public int getSelectedMonth() {
        return getCurrentItemPosition() + 1;
    }
}