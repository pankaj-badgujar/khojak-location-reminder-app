package com.example.khojak.LocationReminder.TODOList;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.khojak.LocationReminder.POJO.PersonalReminder;
import com.example.khojak.R;

import java.util.List;
import java.util.function.Consumer;

public class NotificationService extends Service {

    private final static String channelId = "NOTIFICATION_REMINDER";
    private static NotificationManager notificationManager;
    private static NotificationService context;
    private static Location lastLocation;

    //Consumer to call the personal Reminder for the location
    private Consumer<List<PersonalReminder>> reminder = NotificationService::checkLocation;

    //function to check every notification and to create a notification if necessary.
    private static Consumer<PersonalReminder> createNotificationIfNeeded = (data) -> {
        if (shouldLocationCreateNotification(data.getLocation())) {
            createNotification(data);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence channelName = "Reminder";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500,
                    400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(checkPermission()){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0,
                    1, new LocationTracker());
        }
    }

    private boolean checkPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static void createNotification(PersonalReminder data) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                channelId)
                .setSmallIcon(R.drawable.ic_close_black_24dp)
                .setContentTitle(data.getTitle())
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(3, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static boolean shouldLocationCreateNotification(Location location) {
        return location.distanceTo(lastLocation) <= 1000;
    }

    class LocationTracker implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
            new FetchTaskForService(context, reminder).execute();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    private static void checkLocation(List<PersonalReminder> personalReminders) {
        personalReminders.forEach(createNotificationIfNeeded);
    }
}
