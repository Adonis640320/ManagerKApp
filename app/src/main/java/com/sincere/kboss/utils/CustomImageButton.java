
package com.sincere.kboss.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

/**
 * Created by edap on 13/07/14.
 */

public class CustomImageButton extends ImageButton
{
    public static enum ImageButtonType
    {
        NORMAL, TOOGLE, WITH_CUSTOM_COLOR;
    }

    private ImageButtonType imageButtonType;
    private Animation rotateAnimation;

    public CustomImageButton(Context context)
    {
        super(context);
        setup();
    }

    public CustomImageButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setup();
    }

    public CustomImageButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup()
    {
        imageButtonType = ImageButtonType.NORMAL;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int maskedAction = event.getActionMasked();

        if (maskedAction == MotionEvent.ACTION_DOWN)
        {
            if (imageButtonType == ImageButtonType.NORMAL)
                setColorFilter(Color.argb(150, 155, 155, 155), PorterDuff.Mode.DST_IN);
            else if (imageButtonType == ImageButtonType.WITH_CUSTOM_COLOR)
                setColorFilter(Color.argb(255, 255, 0, 0), PorterDuff.Mode.SRC_ATOP);
            else
                setColorFilter(null);
        }
        else if (maskedAction == MotionEvent.ACTION_UP)
            setColorFilter(null);

        return super.onTouchEvent(event);
    }

    public void setImageButtonType(ImageButtonType type)
    {
        imageButtonType = type;
    }

    public void setRedState(Boolean redState)
    {
        if (redState)
        {
            setColorFilter(Color.argb(255, 255, 0, 0), PorterDuff.Mode.SRC_ATOP);
        }
        else
        {
            setColorFilter(null);
        }
    }

    public void setRotateAnimation()
    {
        if (rotateAnimation == null) {
            rotateAnimation = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setDuration(4000);
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            rotateAnimation.setRepeatMode(Animation.INFINITE);
        }

        startAnimation(rotateAnimation);
    }

    public void stopRotateAnimation()
    {
        if (rotateAnimation != null)
            rotateAnimation.cancel();
    }

    public boolean isRotating() {
        if (rotateAnimation != null)
            return rotateAnimation.hasStarted() & !rotateAnimation.hasEnded();

        return false;
    }
}
