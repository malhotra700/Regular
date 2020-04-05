package com.example.regular;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(preferences.getString("padBitmap","").isEmpty())
            editor.putString("padBitmap","");
        //editor.putInt("SelectedDatePosition",0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();

        if(!preferences.getString("day1","").isEmpty()) {
            if(!preferences.getString("day1","").equals(dateFormat.format(cal.getTime()))) {
                int i=preferences.getInt("AppVisits",0)+1;
                editor.putInt("AppVisits", i);
            }
            if(preferences.getString("day1","").equals(dateFormat.format(cal.getTime()))) {

            }
            else {
                cal.add(Calendar.DATE, -1);
                if (preferences.getString("day1", "").equals(dateFormat.format(cal.getTime()))) {
                    int i = preferences.getInt("DailyStreak", 0) + 1;
                    editor.putInt("DailyStreak", i);
                } else
                    editor.putInt("DailyStreak", 1);
            }
            editor.putBoolean("Done",true);
        }
        else {
            editor.putInt("AppVisits", 1);
            editor.putInt("DailyStreak", 1);
            editor.putBoolean("Done",false);
        }

        cal = Calendar.getInstance();
        editor.putString("day1", dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        editor.putString("day2", dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        editor.putString("day3", dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        editor.putString("day4", dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        editor.putString("day5", dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        editor.putString("day6", dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        editor.putString("day7", dateFormat.format(cal.getTime()));
        editor.apply();

        createNotificationChannel();
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "successful";
                        if (!task.isSuccessful()) {
                            msg = "failed";
                        }
                        //Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        },2500);
    }
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            CharSequence charSequence="Server Notifications";
            String description="Channel for server notifications";
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel("notify1",charSequence,importance);
            channel.setDescription(description);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
