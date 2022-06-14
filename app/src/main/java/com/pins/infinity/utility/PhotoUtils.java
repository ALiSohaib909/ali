package com.pins.infinity.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Pavlo Melnyk on 08.11.2018.
 */
public final class PhotoUtils {

    private static final int QUALITY = 85;

    private PhotoUtils() {
    }

    private static void rotateImage(String photoPath) {
        final int ANGLE = 270;

        Bitmap bmp = BitmapFactory.decodeFile(photoPath);

        if(bmp == null) return;

        Matrix matrix = new Matrix();
        matrix.postRotate(ANGLE);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(new File(photoPath));
            bmp.compress(Bitmap.CompressFormat.JPEG, QUALITY, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap preparePhotoForUpload(File file, Boolean shouldRotate) {
        final int DIMENSION = 200;
        if (shouldRotate) {
            rotateImage(file.getAbsolutePath());
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(options, DIMENSION, DIMENSION);
        options.inJustDecodeBounds = false;

        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, QUALITY, stream);
        byte[] byteArray = stream.toByteArray();

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    /*
     * ******************************************************************************
     *   Copyright (c) 2013-2014 Gabriele Mariotti.
     *
     *   Licensed under the Apache License, Version 2.0 (the "License");
     *   you may not use this file except in compliance with the License.
     *   You may obtain a copy of the License at
     *
     *   http://www.apache.org/licenses/LICENSE-2.0
     *
     *   Unless required by applicable law or agreed to in writing, software
     *   distributed under the License is distributed on an "AS IS" BASIS,
     *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     *   See the License for the specific language governing permissions and
     *   limitations under the License.
     *  *****************************************************************************
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (reqWidth == 0 || reqHeight == 0) return inSampleSize;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
