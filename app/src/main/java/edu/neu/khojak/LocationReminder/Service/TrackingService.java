package edu.neu.khojak.LocationReminder.Service;

import android.Manifest;
import android.app.Notification;
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

import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import edu.neu.khojak.LocationReminder.Util;
import edu.neu.khojak.R;

public class TrackingService extends Service {

    private final static String channelId = "TRACKING_REMINDERS";
    private static NotificationManager notificationManager;
    private static TrackingService context;
    private static Location lastLocation;
    private String username;
    private Consumer<? super Document> createNotificationIfNecessary = this::shouldCreateNotification;

    private void createNotification(Document data) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(data.get("username").toString() + " has reached the destination.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(new Random().nextInt(), notification);
    }

    private void shouldCreateNotification(Document document) {
        Util.userCollection.findOne(new Document("username", document.get("user").toString())).addOnCompleteListener(fetchTask -> {
            if (!fetchTask.isSuccessful() && fetchTask.getResult() == null) {
                return;
            }
            Document fetchedUser = fetchTask.getResult();
            Location location = new Location("");
            if (fetchedUser.get("longitude") == null) {
                return;
            }
            location.setLongitude(Double.parseDouble(fetchedUser.get("longitude").toString()));
            location.setLatitude(Double.parseDouble(fetchedUser.get("latitude").toString()));
            Location reminderLocation = new Location("");
            reminderLocation
                    .setLongitude(Double.parseDouble(fetchedUser.get("longitude").toString()));
            reminderLocation
                    .setLatitude(Double.parseDouble(fetchedUser.get("latitude").toString()));
            if (location.distanceTo(reminderLocation) <= (NotificationService.reminderRadius * 1000)) {
                createNotification(fetchedUser);
                deleteData(document);
            }
        });
    }

    private void deleteData(Document document) {
        Util.trackingCollection.deleteOne(document).addOnCompleteListener(deleteTask -> {
            if(deleteTask.isSuccessful()) {
                Util.userCollection.findOne(new Document("username", Util.userName))
                        .addOnCompleteListener(fetchTask -> {
                   if(fetchTask.isSuccessful() && fetchTask.getResult() != null) {
                       Document user = fetchTask.getResult();
                       List<String> _ids = (List<String>) user.get("trackingIds");
                       _ids.remove(document.get("_id").toString());
                       user.put("trackingIds",_ids);
                       Util.userCollection
                               .updateOne(new Document("username",Util.userName),user)
                               .addOnCompleteListener(updateTask -> {
                                   if(updateTask.isSuccessful()) {
                                       //Do something;
                                   }
                       });
                   }
                });
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        username = Util.userName;
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
        if (checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    60,
                    1, new TrackingService.LocationTracker());
        }
    }

    class LocationTracker implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
            updateUser();
            checkReminder();
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

    private void checkReminder() {
        Util.userCollection.findOne(new Document("username", username)).addOnCompleteListener(fetchTask -> {
            if (!fetchTask.isSuccessful() && fetchTask.getResult() == null) {
                return;
            }
            List<String> _ids = fetchTask.getResult().get("trackingIds") == null ? new ArrayList<>() :
                    (List<String>) fetchTask.getResult().get("trackingIds");

            List<ObjectId> trackingIds = _ids.stream().map(ObjectId::new)
                    .collect(Collectors.toList());

            Document intermediate = new Document("$in", trackingIds);
            Document query = new Document("_id", intermediate);
            RemoteFindIterable<Document> documentRemoteFindIterable = Util.trackingCollection.find(query);
            documentRemoteFindIterable.into(new ArrayList<>()).addOnCompleteListener(task -> {
                if (!task.isSuccessful() && task.getResult() == null) {
                    return;
                }
                List<Document> documents = task.getResult();
                documents.forEach(createNotificationIfNecessary);
            });
        });
    }

    private void updateUser() {
        Util.userCollection.findOne(new Document("username", username)).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Document document = task.getResult();
                document.put("latitude", lastLocation.getLatitude());
                document.put("longitude", lastLocation.getLongitude());
                Util.userCollection.updateOne(new Document("username", username), document);
            }
        });
    }

    private boolean checkPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
