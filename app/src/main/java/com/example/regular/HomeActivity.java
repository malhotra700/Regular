package com.example.regular;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference ref,readRef;
    FirebaseDatabase database;
    GoogleSignInAccount acct;
    TextView headeruserTV;
    CircularImageView circularImageView;
    ImageButton toolbarYourNoteBtn,toolbarQuoteBtn;
    SignaturePad signaturePad;
    Button clearPadBtn,saveQuoteBtn;
    EditText quote;
    Uri imgPath=Uri.parse("android.resource://com.example.regular/"+R.drawable.pic);
    Dialog mdialog,qdialog;
    SharedPreferences preferences;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth .addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        createNotificationChannel();
        Intent myIntent = new Intent(this , NotifyService.class);
        //myIntent.setAction("MY_NOTIFICATION_MESSAGE");
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,16);
        calendar.set(Calendar.MINUTE,35);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);

        if(!preferences.getBoolean("Done",true)){
        database= FirebaseDatabase.getInstance();
        acct = GoogleSignIn.getLastSignedInAccount(HomeActivity.this);
        readRef=database.getReference("Progress");
        readRef.child(acct.getId()+"DailyStreak").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    editor.putInt("DailyStreak", dataSnapshot.getValue(Integer.class));
                    editor.apply();
                }catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        readRef.child(acct.getId()+"AppVisits").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    editor.putInt("AppVisits", dataSnapshot.getValue(Integer.class));
                    Log.i("ffffff",dataSnapshot.getValue(Integer.class)+"");
                    editor.apply();
                }catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        readRef.child(acct.getId()+"MinutesMeditating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    editor.putInt("MinutesMeditating", dataSnapshot.getValue(Integer.class));
                    editor.apply();
                }catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }

        mdialog = new Dialog(this);
        mdialog.setContentView(R.layout.popup_doodle);
        mdialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        mdialog.getWindow().setWindowAnimations(R.style.DialogAnimation1);
        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        qdialog = new Dialog(this);
        qdialog.setContentView(R.layout.popup_quote);
        qdialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        qdialog.getWindow().setWindowAnimations(R.style.DialogAnimation1);
        qdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        //final SharedPreferences.Editor editor = preferences.edit();

        toolbarYourNoteBtn=findViewById(R.id.toolbar_pad_btn);
        toolbarYourNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad=mdialog.findViewById(R.id.doodle_pad);
                String s=preferences.getString("padBitmap","");
                //Log.i("encodedFetch",s);
                if(!s.isEmpty()){
                    byte[] imageAsBytes = Base64.decode(s.getBytes(), Base64.DEFAULT);
                    signaturePad.setSignatureBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                }

                signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
                    @Override
                    public void onStartSigning() {

                    }

                    @Override
                    public void onSigned() {
                        Bitmap bm=signaturePad.getTransparentSignatureBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        String encoded = Base64.encodeToString(b, Base64.DEFAULT);
                        //Log.i("encoded",encoded);
                        editor.putString("padBitmap",encoded);
                        editor.apply();

                    }

                    @Override
                    public void onClear() {
                        editor.putString("padBitmap","");
                        editor.apply();
                    }
                });
                clearPadBtn=mdialog.findViewById(R.id.clear_doodle_btn);
                clearPadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signaturePad.clear();
                    }
                });
                mdialog.show();
            }
        });

        toolbarQuoteBtn=findViewById(R.id.toolbar_quote_btn);
        toolbarQuoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quote=qdialog.findViewById(R.id.quote_edit);
                String s=preferences.getString("quoteFav","");
                //Log.i("encodedFetch",s);
                if(!s.isEmpty()){
                    quote.setText(s);
                }
                saveQuoteBtn=qdialog.findViewById(R.id.save_quote_btn);
                saveQuoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.putString("quoteFav",quote.getText().toString());
                        editor.apply();
                        qdialog.dismiss();
                    }
                });
                qdialog.show();
            }
        });

        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigationView);
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);


        database= FirebaseDatabase.getInstance();
        ref=database.getReference("Events");
        acct = GoogleSignIn.getLastSignedInAccount(this);

        View headerView = navigationView.getHeaderView(0);
        headeruserTV=(TextView)headerView.findViewById(R.id.header_user_tv);
        circularImageView=headerView.findViewById(R.id.user_image);
        imgPath=acct.getPhotoUrl();
        Picasso.get().load(imgPath.toString()).into(circularImageView);

        String curr=getIntent().getExtras().getString("currentUser");
        Log.i("checkUser1",curr);
        headeruserTV.setText(curr.split(" ")[0]);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String val=dateFormat.format(cal.getTime());
        ref.child(acct.getId()).child(val).removeValue();


        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(HomeActivity.this,SignInActivity.class));
                }
            }
        };

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_drawer);
        setUpDrawerContent(navigationView);


        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.l_layout, new EventsFragment());
        tx.commit();

    }
    public void selectItemDrawer(MenuItem menuItem){
        Fragment myfragment=null;
        Class fragmentClass=EventsFragment.class;
        switch (menuItem.getItemId()){
            case R.id.events_nav:
                fragmentClass=EventsFragment.class;
                break;
            case R.id.tasks_nav:
                fragmentClass=TasksFragment.class;
                break;
            case R.id.health_nav:
                fragmentClass=HealthFragment.class;
                break;
            case R.id.progress_nav:
                fragmentClass=ProgressFragment.class;
                break;
            default:
                fragmentClass=EventsFragment.class;
                break;
        }
        try{
            myfragment=(Fragment)fragmentClass.newInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.l_layout,myfragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();

    }

    private void setUpDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.logout){
                    mAuth.signOut();
                }
                selectItemDrawer(menuItem);
                return true;
            }
        });
    }
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            CharSequence charSequence="Notifications";
            String description="Channel for notifications";
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel("notify",charSequence,importance);
            channel.setDescription(description);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
