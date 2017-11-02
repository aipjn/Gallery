package com.javano1.gallery.view;

import android.content.Context;
import android.util.AttributeSet;

public class GImageView extends android.support.v7.widget.AppCompatImageView {

    public GImageView(Context context) {
        super(context);
    }

    public GImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
