package com.example.bugtracker;

import static com.example.bugtracker.ApplicationController.compareLists;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


    import android.app.Notification;
import android.app.NotificationChannel; import android.app.NotificationManager; import android.app.PendingIntent; import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.util.Timer; import java.util.TimerTask;
    public class MyService extends Service {
        private Timer timer;
        private static final String CHANNEL_ID = "1";

        private String URL_STRING;
        @Override
        public void onCreate() {
            Log.d("News Reader ", "Service created");
            startTimer();
        }
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d("News Reader ", "Service started");
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "FG Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class); manager.createNotificationChannel(serviceChannel);
            Intent notifcation_intent = new Intent(this,  LoginActivity.class);
            notifcation_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifcation_intent, PendingIntent.FLAG_MUTABLE);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID) .setContentTitle("Foreground Service")
                    .setContentText("Click here to login again!") .setSmallIcon(R.drawable.ic_launcher_background) .setContentIntent(pendingIntent)
                    .build();
            startForeground(2, notification);
            return START_STICKY; }
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            Log.d("News Reader ", "Service bound - Not used");
            return null; }
        @Override
        public void onDestroy() {
            Log.d("News Reader ", "Service destroyed ");
            stopTimer(); }
        private void startTimer() {
            TimerTask task = new TimerTask() { @Override
            public void run() {
                FireStoreServices.getActiveBugsList();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
               //Log.d("BUG LIST", ApplicationController.newBugList.toString());
                //Log.d("Main Bug List", ApplicationController.AllActiveBugs.toString());

                Log.d("BUG LIST", ApplicationController.newBugList.toString());
                 if (!compareLists()) {

                    sendNotification("New bugs are available!"); } else
                    Log.d("Bugs", "No Updates possible"); }
            };
            timer = new Timer(true);
            int delay = 1;
            int interval = 5000;
            timer.schedule(task, delay, interval);
        }
        private void stopTimer() { if (timer != null) {
            timer.cancel(); }
        }
        private void sendNotification(String text) {
            Intent notificationIntent = new Intent(this,
            LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

            CharSequence tickerText = "Bugs updated!";
            CharSequence contentTitle = "Bugtracker";
            CharSequence contentText = text;
            NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID", "NotifName", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel); Notification notifacton = new NotificationCompat
                    .Builder(this, "CHANNEL_ID") .setSmallIcon(R.drawable.ic_launcher_background).setTicker(tickerText) .setContentTitle(contentTitle) .setContentText(contentText) .setContentIntent(pendingIntent) .setAutoCancel(true) .setChannelId("CHANNEL_ID") .build();
            final int NOTIFICATION_ID = 1; manager.notify(NOTIFICATION_ID, notifacton);
        } }