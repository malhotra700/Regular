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

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference ref;
    FirebaseDatabase database;
    GoogleSignInAccount acct;
    TextView headeruserTV;
    ImageButton toolbarYourNoteBtn;
    SignaturePad signaturePad;
    Button clearPadBtn;
    Dialog mdialog;
    SharedPreferences preferences;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        mdialog = new Dialog(this);
        mdialog.setContentView(R.layout.popup_doodle);
        mdialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

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

        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigationView);
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String val=dateFormat.format(cal.getTime());
        database= FirebaseDatabase.getInstance();
        ref=database.getReference("Events");
        acct = GoogleSignIn.getLastSignedInAccount(this);

        View headerView = navigationView.getHeaderView(0);
        headeruserTV=(TextView)headerView.findViewById(R.id.header_user_tv);
        String curr=getIntent().getExtras().getString("currentUser");
        Log.i("checkUser1",curr);
        headeruserTV.setText("Hey! "+curr);


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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
