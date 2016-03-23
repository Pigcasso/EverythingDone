package com.ywwynm.everythingdone.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * Created by ywwynm on 2015/9/23.
 * Utils for Bitmap
 */
public class BitmapUtil {

    public static final String TAG = "EverythingDone$BitmapUtil";

    private static int calculateInSampleSize(int oWidth, int oHeight, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (oHeight > reqWidth || oWidth > reqHeight) {
            final int halfHeight = oHeight / 2;
            final int halfWidth = oWidth / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap createLayeredBitmap(Drawable d, int color) {
        ColorDrawable cd = new ColorDrawable(color);
        LayerDrawable lb = new LayerDrawable(new Drawable[] { cd, d });

        int w = d.getIntrinsicWidth();
        int h = d.getIntrinsicHeight();
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        lb.setBounds(0, 0, w, h);
        lb.draw(new Canvas(bm));

        return bm;
    }

    public static Bitmap createScaledBitmap(Bitmap src, int reqWidth, int reqHeight, boolean inside) {
        int oWidth  = src.getWidth();
        int oHeight = src.getHeight();

        Bitmap dst;
        if (inside && oWidth <= reqWidth && oHeight <= reqHeight) {
            dst = src;
        } else {
            float fW = (float) oWidth  / reqWidth;
            float fH = (float) oHeight / reqHeight;
            int maintainedSide;
            if (inside) {
                maintainedSide = fW >= fH ? oWidth : oHeight;
            } else {
                maintainedSide = fW <= fH ? oWidth : oHeight;
            }
            if (maintainedSide == oWidth) {
                dst = Bitmap.createScaledBitmap(src, reqWidth, (int) (oHeight / fW), !inside);
            } else {
                dst = Bitmap.createScaledBitmap(src, (int) (oWidth / fH), reqHeight, !inside);
            }
        }
        if (src != dst) {
            src.recycle();
        }
        return dst;
    }

    public static Bitmap createCroppedBitmap(Bitmap src, int reqWidth, int reqHeight) {
        Bitmap scaledBm = createScaledBitmap(src, reqWidth, reqHeight, false);

        int x = 0, y = 0;
        int oWidth  = scaledBm.getWidth();
        int oHeight = scaledBm.getHeight();

        if (reqWidth < oWidth) {
            x = (oWidth - reqWidth) / 2;
        }
        if (reqHeight < oHeight) {
            y = (oHeight - reqHeight) / 2;
        }

        Bitmap croppedBm = Bitmap.createBitmap(scaledBm, x, y, reqWidth, reqHeight);
        if (scaledBm != croppedBm) {
            scaledBm.recycle();
        }
        return croppedBm;
    }

    public static Bitmap decodeFileWithRequiredSize(String pathName, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        int oWidth  = options.outWidth;
        int oHeight = options.outHeight;

        options.inSampleSize = calculateInSampleSize(oWidth, oHeight, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);

        return createCroppedBitmap(src, reqWidth, reqHeight);
    }

    public static Bitmap decodeFileFitsSize(String pathName, int fWidth, int fHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        Bitmap bm;
        int oWidth  = options.outWidth;
        int oHeight = options.outHeight;

        if (oWidth >= fWidth && oHeight >= fHeight) {
            options.inSampleSize = calculateInSampleSize(oWidth, oHeight, fWidth, fHeight);
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(pathName, options);
        } else {
            bm = BitmapFactory.decodeFile(pathName);
        }
        return createScaledBitmap(bm, fWidth, fHeight, true);
    }
}