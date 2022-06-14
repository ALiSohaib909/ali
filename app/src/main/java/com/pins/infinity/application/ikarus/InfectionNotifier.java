package com.pins.infinity.application.ikarus;

/**
 * Created by Pavlo Melnyk on 31.07.2018.
 */
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.ikarussecurity.android.malwaredetection.IkarusMalwareDetection;
import com.ikarussecurity.android.malwaredetection.IkarusScanListener;
import com.ikarussecurity.android.malwaredetection.Infection;
import com.ikarussecurity.android.malwaredetection.ScanEvent;
import com.pins.infinity.R;

import java.util.List;

// This class is a very simple IkarusScanListener implementation. It is
// instantiated in TestApplication's onCreate method and is never destroyed,
// so it will keep receiving scan-related events for the entire duration of the
// app.
//
// When an infection is found, its onInfectionFound is called and displays a
// simple notification.

public class InfectionNotifier implements IkarusScanListener {

    private final Context context;
    private final Handler handler = new Handler();

    public InfectionNotifier(Context context) {
        this.context = context;
    }

    @Override
    public void onInfectionFound(ScanEvent event) {

        // It is good practice to fetch the infection list once and then reuse
        // it. Subsequent calls to IkarusMalwareDetection.getAllInfections
        // may yield different results.

        List<Infection> allInfections = IkarusMalwareDetection.getAllInfections();
        if (allInfections.isEmpty()) {
            return;
        }

        // The concrete type of the object returned by event.getScanData depends
        // on which listener method is being executed. In onInfectionFound, an
        // Infection object representing the currently
        // found infection is returned:

        final Infection infection = (Infection) event.getScanData();

        handler.post(() -> Toast.makeText(context, context.getString(R.string.ikarus_infection_found_in_file) + infection.getFilePath().getAbsolutePath(),
                Toast.LENGTH_LONG).show());
    }

    @Override
    public void onIgnoreListModified(ScanEvent event) {
    }

    @Override
    public void onInfectionRemoved(ScanEvent event) {
    }

    @Override
    public void onScanCompleted(ScanEvent event) {
    }

    @Override
    public void onScanProgress(ScanEvent event) {
    }

    @Override
    public void onScanStarted(ScanEvent event) {
    }

}
