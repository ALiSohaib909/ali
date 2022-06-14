package com.pins.infinity.utility;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.pins.infinity.utility.SharedPreferences.Const;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pavlo Melnyk on 29.08.2018.
 */
public final class Base64Utils {
    private Base64Utils() {
    }

    public static String toBase64(String path) {
        try {
            InputStream inputStream = new FileInputStream(path);
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            bytes = output.toByteArray();
            inputStream.close();

            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e("Base64Utils","Photo in Base64Utils error " + e.toString());
            return Const.STRING_EMPTY;
        }
    }

    public static String toBase64(Bitmap bitmap) {

        if (bitmap == null) {
            return Const.STRING_EMPTY;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    static byte[] decodeBase64(String base64) {
        if (base64 != null) {
            return Base64.decode(base64, Base64.DEFAULT);
        }

        return null;
    }
}

