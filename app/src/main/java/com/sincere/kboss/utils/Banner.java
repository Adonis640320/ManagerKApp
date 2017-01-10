package com.sincere.kboss.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.sincere.kboss.R;

/**
 * Created by Michael on 2016.09.19.
 */
public class Banner extends View implements ImageAware {

    Context ctx;
    private Drawable logo;

    public Banner(Context context) {
        super(context);

        ctx = context;
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);

        ctx = context;
    }

    public Banner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ctx = context;
    }

    @Override protected void onMeasure(int widthMeasureSpec,
                                       int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if (logo == null) {
            logo = ctx.getResources().getDrawable(R.drawable.ic_empty);
        }
        int height = width * logo.getIntrinsicHeight() / logo.getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }

    public void setImageResource(int res) {
        logo = ctx.getResources().getDrawable(res);
        setBackgroundDrawable(logo);
    }

    // ImageAware functions

    @Override
    public boolean setImageDrawable(Drawable drawable) {
        logo = drawable;
        setBackgroundDrawable(logo);
        return true;
    }

    @Override
    public View getWrappedView() {
        return this;
    }

    @Override
    public boolean isCollected() {
        return false;
    }

    @Override
    public boolean setImageBitmap(Bitmap bitmap) {
        logo = new BitmapDrawable(bitmap);
        setBackgroundDrawable(logo);
        return true;
    }

    @Override
    public ViewScaleType getScaleType() {
        return ViewScaleType.FIT_INSIDE;
    }
}
