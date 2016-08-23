package com.csipsimple.f5chat.font_family;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;


public class Font_Welcome_scr_et_light extends EditText {

    public Font_Welcome_scr_et_light(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Font_Welcome_scr_et_light(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Font_Welcome_scr_et_light(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/opensanslight.ttf");
            setTypeface(tf);
            setAlpha(0.75F);
        }
    }
}