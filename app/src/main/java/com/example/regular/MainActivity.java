package com.example.regular;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

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

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
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
}
