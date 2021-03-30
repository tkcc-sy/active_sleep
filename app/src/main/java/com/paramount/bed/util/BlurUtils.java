package com.paramount.bed.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.annotation.FloatRange;
import androidx.annotation.MainThread;
import android.view.View;

import jp.wasabeef.picasso.transformations.internal.FastBlur;

public class BlurUtils {
    @MainThread
    public static Bitmap blur(Context context, View view, @FloatRange(from = 0, to = 25) float radius) {
        Bitmap sourceBitmap = getScreenshot(view);
        return blur(context, sourceBitmap, radius);
    }

    public static Bitmap blur(Context context, Bitmap origin, @FloatRange(from = 0, to = 25) float radius) {

        Bitmap scaled = Bitmap.createScaledBitmap(origin, origin.getWidth(), origin.getHeight(), false);
        Bitmap output = Bitmap.createBitmap(scaled);
        output = FastBlur.blur(output,(int)radius, true);
        return output;
    }

    @MainThread
    private static Bitmap getScreenshot(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }
}