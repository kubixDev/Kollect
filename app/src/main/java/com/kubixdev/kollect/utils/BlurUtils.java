package com.kubixdev.kollect.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

// using bitmap because RenderEffect is only available for Android API 31+
public class BlurUtils {
    private static final float BLUR_RADIUS = 25f;

    public static Bitmap blur(Context context, Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleRatio = Math.min(1f, (float) 1024 / Math.max(width, height));

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(width * scaleRatio), Math.round(height * scaleRatio), false);
        Bitmap outputBitmap = Bitmap.createBitmap(resizedBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, resizedBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        script.setRadius(BLUR_RADIUS);
        script.setInput(tmpIn);
        script.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        rs.destroy();
        return outputBitmap;
    }

    public static void applyBlur(Context context, ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap blurredBitmap = blur(context, bitmap);
        imageView.setImageBitmap(blurredBitmap);
    }

    public static void unblur(ImageView imageView, Bitmap originalBitmap) {
        imageView.setImageBitmap(originalBitmap);
    }
}