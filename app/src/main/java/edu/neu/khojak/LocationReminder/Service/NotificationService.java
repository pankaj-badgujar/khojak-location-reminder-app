package edu.neu.khojak.LocationReminder.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import edu.neu.khojak.LocationReminder.DAO.UserDAO;
import edu.neu.khojak.LocationReminder.Database.UserDatabase;
import edu.neu.khojak.LocationReminder.POJO.PersonalReminder;
import edu.neu.khojak.LocationReminder.POJO.User;
import edu.neu.khojak.LocationReminder.TODOList.DeleteTask;
import edu.neu.khojak.LocationReminder.TODOList.FetchTaskForService;
import edu.neu.khojak.LocationReminder.TODOList.ReminderLocationView;
import edu.neu.khojak.LocationReminder.Util;
import edu.neu.khojak.LoginActivity;
import edu.neu.khojak.R;

public class NotificationService extends Service {

    private final static String channelId = "NOTIFICATION_REMINDER";
    private static NotificationManager notificationManager;
    private static NotificationService context;
    private static Location lastLocation;
    public String username;
    private final static String notificationTitle = "Notification from Kojak";
    public static int reminderRadius = 1;

    //Consumer to call the personal Reminder for the location
    private Consumer<List<PersonalReminder>> reminder = NotificationService::checkLocation;

    //function to check every notification and to create a notification if necessary.
    private static Consumer<PersonalReminder> createNotificationIfNeeded = (data) -> {
        if (shouldLocationCreateNotification(data.getLocation())) {
            createNotification(data);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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
                    60,
                    1, new LocationTracker());
        }
    }

    private boolean checkPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static void createNotification(PersonalReminder data) {
        Intent intent = new Intent(context,ReminderLocationView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("reminder", data);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(data.getTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        new DeleteTask(context,data).execute();
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(data.getId(), notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        (new FetchTask()).execute(this);
        return START_STICKY;
    }

    private class FetchTask extends AsyncTask<NotificationService, Void, Void> {
        @Override
        protected Void doInBackground(NotificationService... loginActivities) {
            UserDAO dao = UserDatabase.getInstance(loginActivities[0]).getUserDao();
            List<User> user = dao.getAllUsers();
            if(user.size() > 0) {
                username = user.get(0).getUsername();
            } else {
                stopSelf();
            }
            return null;
        }
    }

    public String getUsername() {
        return this.username;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static boolean shouldLocationCreateNotification(Location location) {
        return location.distanceTo(lastLocation) <= ( reminderRadius * 1000 );
    }

    class LocationTracker implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
            new FetchTaskForService(context, reminder).execute();
            checkGroupReminders();
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

    private void checkGroupReminders() {
        Util.userCollection.findOne(new Document("username", Util.userName)).addOnCompleteListener(userObjectFetch -> {
            if( !userObjectFetch.isSuccessful() || userObjectFetch.getResult() == null) {
                return;
            }
            Document user = userObjectFetch.getResult();
            if(user.get("groupIds") == null) {
                return;
            }
            List<String> _ids = (List<String>) user.get("groupIds");
            List<ObjectId> objectIds = _ids.stream().map(ObjectId::new).collect(Collectors.toList());
            Document intermediate = new Document("$in",objectIds);
            Document query = new Document("_id",intermediate);
            RemoteFindIterable<Document> documentRemoteFindIterable = Util.groupCollection.find(query);
            documentRemoteFindIterable.into(new ArrayList<>()).addOnCompleteListener(groupFetch -> {
               if(!groupFetch.isSuccessful() || groupFetch.getResult() == null) {
                   return;
               }
               fetchReminders(groupFetch.getResult());
            });
        });
    }

    private void fetchReminders(ArrayList<Document> result) {
        List<String> _ids = new ArrayList<>();
        result.forEach(document -> {
            _ids.addAll(document.get("reminderIds") == null ? new ArrayList<>() :
                    (List<String>) document.get("reminderIds"));
        });
        List<ObjectId> objectIds = _ids.stream().map(ObjectId::new).collect(Collectors.toList());;
        Document intermediate = new Document("$in",objectIds);
        Document query = new Document("_id",intermediate);
        RemoteFindIterable<Document> documentRemoteFindIterable = Util.reminderCollection.find(query);
        documentRemoteFindIterable.into(new ArrayList<>()).addOnCompleteListener(reminderFetch -> {
            if(!reminderFetch.isSuccessful() || reminderFetch.getResult() == null) {
                return;
            }
            List<Document> data = reminderFetch.getResult();
            checkLocationForGroup(data);
        });
    }

    private void checkLocationForGroup(List<Document> data) {
        data.forEach(remoteReminder -> {
            double latitude = Double.parseDouble((String) remoteReminder.get("latitude"));
            double longitude = Double.parseDouble((String) remoteReminder.get("longitude"));
            Location location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            if(shouldLocationCreateNotification(location)) {
                createNotificationForGroup(remoteReminder);
            }
        });
    }

    private void createNotificationForGroup(Document remoteReminder) {
        Intent intent = new Intent(context,ReminderLocationView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("reminder", new PersonalReminder(remoteReminder));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteReminder.get("groupName").toString())
                .setContentText(remoteReminder.get("title").toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Util.removeReminder(remoteReminder);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(new Random().nextInt(), notification);
    }

    private static void checkLocation(List<PersonalReminder> personalReminders) {
        personalReminders.forEach(createNotificationIfNeeded);
    }
}
