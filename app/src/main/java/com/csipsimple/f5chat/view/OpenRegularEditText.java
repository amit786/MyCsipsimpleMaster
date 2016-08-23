package com.csipsimple.f5chat.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by HP on 21-05-2016.
 */
public class OpenRegularEditText extends EditText {
    public OpenRegularEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenRegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenRegularEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "" +
                        "fonts/opensansregular.ttf");
        setTypeface(tf);
    }
}
