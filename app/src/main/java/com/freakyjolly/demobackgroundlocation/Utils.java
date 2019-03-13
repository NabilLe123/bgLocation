package com.freakyjolly.demobackgroundlocation;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by FreakyJolly on 01-04-2018.
 */

public class Utils {
    private static final String TAG = "UtilsClass";
    final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";
    public static float accuracy;
    static String addressFragments = "";
    static List<Address> addresses = null;
    public static final long UPDATE_INTERVAL = 5 * 1000;
    public static final float SMALLEST_DISPLACEMENT = 1.0F;
    public static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    public static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 2;

    static void setLocationUpdatesResult(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, value)
                .apply();
    }

    @SuppressLint("MissingPermission")
    public static void getLocationUpdates(final Context context, final Intent intent, String broadcastevent) {

        LocationResult result = LocationResult.extractResult(intent);
        if (result != null) {

            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
            String nowDate = formatter.format(today);


            List<Location> locations = result.getLocations();
            Location firstLocation = locations.get(0);

            getAddress(firstLocation, context);
            //firstLocation.getAccuracy();
            //firstLocation.getLatitude();
            //firstLocation.getLongitude();
            //firstLocation.getAccuracy();
            //firstLocation.getSpeed();
            //firstLocation.getBearing();
            LocationRequestHelper.getInstance(context).setValue("locationTextInApp", "You are at " + getAddress(firstLocation, context) + "(" + nowDate + ") with accuracy " + firstLocation.getAccuracy() + " Latitude:" + firstLocation.getLatitude() + " Longitude:" + firstLocation.getLongitude() + " Speed:" + firstLocation.getSpeed() + " Bearing:" + firstLocation.getBearing());
            showNotificationOngoing(context, broadcastevent, "");
        }
    }


    public static String getAddress(Location location, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        // Address found using the Geocoder.
        addresses = null;
        Address address = null;
        addressFragments = "";
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            address = addresses.get(0);
        } catch (IOException ioException) {
            Log.e(TAG, "error", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e(TAG, "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0) {
            Log.i(TAG, "ERORR");
            addressFragments = "NO ADDRESS FOUND";
        } else {
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments = addressFragments + String.valueOf(address.getAddressLine(i));
            }
        }
        LocationRequestHelper.getInstance(context).setValue("addressFragments", addressFragments);
        return addressFragments;
    }

    public static void showNotificationOngoing(Context context, String broadcastevent, String title) {
        Log.d("aaaaaaaaaaaaaa", "broadcastevent "+broadcastevent+" title "+title);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "NabilDriver")
                .setContentTitle(title + DateFormat.getDateTimeInstance().format(new Date()) + ":" + accuracy)
                .setContentText(addressFragments.toString())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                //.setStyle(new Notification.BigTextStyle().bigText(addressFragments.toString()))
                .setAutoCancel(true);
        //.setContentIntent(pendingIntent);

        notificationManager.notify(3, notificationBuilder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("NabilDriver", "NabilDriver_channel", NotificationManager.IMPORTANCE_HIGH));
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    public static void removeNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}

