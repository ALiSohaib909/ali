package com.pins.infinity.utility;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by Pavlo Melnyk on 29.08.2018.
 */
public final class DialogUtils {

    private DialogUtils(){
    }
    //TODO will be refectored when Ikarus will be refectored

    public static void showAlertOkDialog(Context context, @Nullable String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                })
                .show();
    }
}
