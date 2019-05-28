package com.example.notepadproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private String dbName = "MyNotes";
    private String dbTable = "Notes";
    private String colID = "ID";
    private String colTitle = "Title";
    private String colDesc = "Description";
    private String colPriority = "Priority";
    private String colLocation = "Location";
    private String colDate = "Date";
    private String colAlarmDate = "AlarmDate";
    private String colHtmlDesc = "HtmlDesc";
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null && intent.hasExtra("id")){
            createNotification(intent.getExtras().getInt("id"), context);
        }
    }

    private void createNotification(int id_, Context context) {
        String id = "usame";
        String title ="Notepad App Notification";
        DbManager dbManager = new DbManager(context);
        String[] projections = {colID, colTitle, colDesc, colPriority, colDate, colAlarmDate, colLocation, colHtmlDesc};
        String[] selectionArgs = {"%"};
        Cursor cursor = dbManager.query(projections, "ID LIKE ?", selectionArgs, colID);
        Note nt = dbManager.getNote(cursor, id_);
        if(nt == null)
            return;

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notificationManager == null) {
            notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mChannel.enableLights(true);


                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, TextViewActivity.class);
            intent.putExtra("id", id_);
            intent.putExtra("html_desc", nt.getNoteHtmlDesc());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, nt.getNoteID(), intent, 0);
            builder.setContentTitle(nt.getNoteName())
                    .setSmallIcon(R.drawable.ic_action_add)
                    .setColor(ContextCompat.getColor(context, R.color.dark_pink))
                    .setContentText("You have a job to do!")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker("ticker")
                    .setLights(ContextCompat.getColor(context, R.color.dark_pink), 100, 50)
                    .setSound(alarmSound)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, TextViewActivity.class);
            intent.putExtra("id", id_);
            intent.putExtra("html_desc", nt.getNoteHtmlDesc());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context,  nt.getNoteID(), intent, 0);
            builder.setContentTitle(nt.getNoteName())
                    .setSmallIcon(R.drawable.ic_action_alarm)
                    .setColor(ContextCompat.getColor(context, R.color.dark_pink))
                    .setContentText("You have a reminder!")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker("ticker")
                    .setLights(ContextCompat.getColor(context, R.color.dark_pink), 100, 50)
                    .setSound(alarmSound)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notificationManager.notify(id_, notification);
    }
}
