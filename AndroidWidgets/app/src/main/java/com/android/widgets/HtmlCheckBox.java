package com.android.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Html;
import android.util.AttributeSet;

public class HtmlCheckBox extends AppCompatCheckBox {
    private String mText;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public HtmlCheckBox(Context context) {
        super(context);
        init(null);
    }

    public HtmlCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HtmlCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HtmlCheckBox);
            mText = typedArray.getString(R.styleable.HtmlCheckBox_cbHtmlText);
            typedArray.recycle();
        }
        if (mText == null) mText = "";
        setTextHtml(mText);
    }

    public void setTextHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        else
            setText(Html.fromHtml(text));
    }

    @Override
    public void setOnCheckedChangeListener(@Nullable OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
        super.setOnCheckedChangeListener(listener);
    }

    private void setCheckedSilent(boolean isChecked) {
        super.setOnCheckedChangeListener(null);
        setChecked(isChecked);
        super.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.isChecked = isChecked();
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCheckedSilent(ss.isChecked);
    }

    static class SavedState extends BaseSavedState {
        boolean isChecked;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            isChecked = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isChecked ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
