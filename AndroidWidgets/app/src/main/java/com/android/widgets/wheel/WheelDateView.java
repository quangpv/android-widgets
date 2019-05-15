package com.android.widgets.wheel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;


import com.android.widgets.R;
import com.android.widgets.wheel.drawable.SelectedDrawable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WheelDateView extends LinearLayout {

    private SelectedDrawable mSelected = new SelectedDrawable();

    private OnDateSelectedListener mOnDateSelectedListener;

    private WheelYearView mPickerYear;
    private WheelMonthView mPickerMonth;
    private WheelDayView mPickerDay;

    private int mYear;
    private int mMonth;
    private int mDay;

    public WheelDateView(Context context) {
        this(context, null);
    }

    public WheelDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_wheel_date_picker, this);

        mPickerYear = findViewById(R.id.wYear);
        mPickerMonth = findViewById(R.id.wMonth);
        mPickerDay = findViewById(R.id.wDay);

        setMaximumWidthTextYear();
        mPickerMonth.setMaximumWidthText("00");
        mPickerDay.setMaximumWidthText("00");

        mSelected.setColor(getResources().getColor(R.color.colorPrimary));
        mSelected.setIndicatorHeight(getResources().getDimensionPixelSize(R.dimen.size_of_wheel_indicator));

        initYears();

        mYear = mPickerYear.getSelectedYear();
        mMonth = mPickerMonth.getSelectedMonth();
        mDay = mPickerDay.getSelectedDay();
        setup();
    }

    private void setup() {
        mPickerYear.setOnItemSelectedListener((view, data, position) -> {
            mYear = mPickerYear.getSelectedYear();
            mPickerDay.setYear(mYear);
            if (mOnDateSelectedListener != null)
                mOnDateSelectedListener.onDateSelected(getSelectedDateString());
        });
        mPickerMonth.setOnItemSelectedListener((view, data, position) -> {
            mMonth = mPickerMonth.getSelectedMonth();
            mPickerDay.setMonth(mMonth);
            if (mOnDateSelectedListener != null)
                mOnDateSelectedListener.onDateSelected(getSelectedDateString());
        });
        mPickerDay.setOnItemSelectedListener((view, data, position) -> {
            mDay = mPickerDay.getSelectedDay();
            if (mOnDateSelectedListener != null)
                mOnDateSelectedListener.onDateSelected(getSelectedDateString());
        });
    }

    private void initYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        mPickerYear.setYear(currentYear - 150, currentYear + 150, currentYear);
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
        int itemHeight = mPickerDay.getItemHeight();
        mSelected.layout(l, middleY - itemHeight / 2, r, middleY + itemHeight / 2);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mSelected.draw(canvas);
    }

    private void setMaximumWidthTextYear() {
        List years = mPickerYear.getData();
        String lastYear = String.valueOf(years.get(years.size() - 1));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lastYear.length(); i++)
            sb.append("0");
        mPickerYear.setMaximumWidthText(sb.toString());
    }

    private String getSelectedDateString() {
        return mPickerMonth.getData().get(mPickerMonth.getSelectedMonth() - 1) + ", " + mDay + ", " + mYear;
    }

    @Override
    public String toString() {
        return mYear + "-" + mMonth + "-" + mDay;
    }

    public void setItemTextColor(int color) {
        mPickerYear.setItemTextColor(color);
        mPickerMonth.setItemTextColor(color);
        mPickerDay.setItemTextColor(color);
    }

    public void setTypeface(Typeface tf) {
        mPickerYear.setTypeface(tf);
        mPickerMonth.setTypeface(tf);
        mPickerDay.setTypeface(tf);
    }

    public Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mYear);
        calendar.set(Calendar.MONTH, mMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, mDay);
        return calendar.getTime();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
    }

    public void initYearFromNow() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        mPickerYear.setYear(currentYear, currentYear + 100, currentYear);
    }

    public void initYearBeforeNow() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        mPickerYear.setYear(currentYear - 100, currentYear, currentYear);
    }

    public interface OnDateSelectedListener {
        void onDateSelected(String date);
    }
}