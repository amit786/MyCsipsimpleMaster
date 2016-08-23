package com.csipsimple.f5chat.font_family;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by SHRIG on 5/23/2016.
 */
public class Font_welcome_scr_Tv_regular extends TextView {


    public Font_welcome_scr_Tv_regular(Context context) {
        super(context);
        init();
    }

    public Font_welcome_scr_Tv_regular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Font_welcome_scr_Tv_regular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Font_welcome_scr_Tv_regular(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/opensansregular.ttf");
            setTypeface(tf);
            setAlpha(0.75F);
        }
    }
}
