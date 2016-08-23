package com.csipsimple.f5chat.utility;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

public class RoundedCornersDrawable extends BitmapDrawable {

	private final BitmapShader bitmapShader;
	private final Paint p;
	private final RectF rect;
	private final float borderRadius;

	public RoundedCornersDrawable(final Resources resources, final Bitmap bitmap) 
	{
		super(resources, bitmap);
		bitmapShader = new BitmapShader(getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		final Bitmap b = getBitmap();
		p = getPaint();
		p.setAntiAlias(true);
		p.setShader(bitmapShader);
		p.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		final int w = b.getWidth();
		final int h = b.getHeight();
		rect = new RectF(0, 0, w, h);
		borderRadius = 0.25f * Math.min(w, h);
	}

	@Override
	public void draw(final Canvas canvas) {
		canvas.drawRoundRect(rect, borderRadius, borderRadius, p);
	}
}
