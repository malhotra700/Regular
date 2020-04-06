package com.example.regular;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifyService extends BroadcastReceiver {
    SharedPreferences prefs;
    public NotifyService() {
    }
    @Override
    public void onReceive(Context context,Intent intent){
        Log.i("Notifyyy", "Alarm");
        prefs = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        //final SharedPreferences.Editor editor = prefs.edit();
        //NotificationManager notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context,MainActivity.class);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"notify")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_calendar)
                .setContentTitle("Today's Events")
                .setContentText(prefs.getString("TodayEvents",""))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(100,builder.build());
        //if (intent.getAction().equals("MY_NOTIFICATION_MESSAGE")) {
          //  notificationManager.notify(100,builder.build());
        Log.i("Notifyyy", "Alarm"); //Optional, used for debuging.
        //}

    }
}
