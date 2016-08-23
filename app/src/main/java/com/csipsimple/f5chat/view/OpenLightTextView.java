package com.csipsimple.f5chat.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by HP on 21-05-2016.
 */
public class OpenLightTextView extends TextView {
    public OpenLightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenLightTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "" +
                        "fonts/opensanslight.ttf");
        setTypeface(tf);
    }
}
