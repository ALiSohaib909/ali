package com.pins.infinity.application.ikarus;

/**
 * Created by Pavlo Melnyk on 31.07.2018.
 */
import android.content.Context;
import android.widget.Toast;

import com.ikarussecurity.android.malwaredetection.IkarusWebFilteringListener;
import com.ikarussecurity.android.malwaredetection.InfectedWebPageBlockedEvent;
import com.ikarussecurity.android.malwaredetection.InfectedWebPageFoundEvent;

// This class is a very simple IkarusWebFilteringListener implementation. It is
// instantiated in TestApplication's onCreate method and is never destroyed,
// so it will keep receiving events related to web filtering for the entire
// duration of the
// app.
//
// When a malicious web page is found or blocked is found (depending on the
// value set in IkarusMalwareDetection.setWebFilteringMode), its
// onInfectedPageBlocked or onInfectedPageFound method is called and displays a
// simple notification.

public final class MaliciousWebPageNotifier implements IkarusWebFilteringListener {

    private final Context context;

    public MaliciousWebPageNotifier(Context context) {
        this.context = context;
    }

    @Override
    public void onInfectedPageBlocked(InfectedWebPageBlockedEvent event) {
        Toast.makeText(context, "Malicious web page unloaded: " + event.getUrl() + ", " + event.getBrowserPackage(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInfectedPageFound(InfectedWebPageFoundEvent event) {
        Toast.makeText(context, "Malicious web page found: " + event.getUrl(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInfectedPageBlockedByUser(InfectedWebPageFoundEvent event) { }

    @Override
    public void onInfectedPageIgnoredByUser(InfectedWebPageFoundEvent event) { }

}
