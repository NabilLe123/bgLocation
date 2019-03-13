package com.freakyjolly.demobackgroundlocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;


public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LUBroadcastReceiver";
    public static final String ACTION_PROCESS_UPDATES =
            "com.freakyjolly.demobackgroundlocation.action" + ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("aaaaaaaaaaaaaa", "onReceive");
        if (intent != null) {
            Log.d("aaaaaaaaaaaaaa", "onReceive intent "+intent.getAction());

            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                Utils.setLocationUpdatesResult(context, DateFormat.getDateTimeInstance().format(new Date()));
                Utils.getLocationUpdates(context, intent, "PROCESS_UPDATES");

            }
        }else
        {
            Log.d("aaaaaaaaaaaaaa", "onReceive intent null");
        }
    }
}

