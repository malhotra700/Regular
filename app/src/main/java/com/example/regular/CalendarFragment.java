package com.example.regular;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    HorizontalCalendar horizontalCalendar;
    Spinner spinner;
    Dialog mdialog;
    FloatingActionButton floatingActionButton;
    EditText eventHeading;
    DatePicker eventDatePicker;
    TimePicker startTimePicker,endTimePicker;
    Events events;
    Switch aSwitch,starSwitch;
    TextView startTimeTV,endTimeTV;

    FirebaseDatabase database;
    DatabaseReference ref,readRef;
    GoogleSignInAccount acct;
    RecyclerView recyclerView;
    ArrayList<Events> list;
    SharedPreferences preferences;
    String selectedDate;
    int Checker;
    SharedPreferences.Editor editor;
    private Context mContext;
    private AppCompatActivity mActivity;
    private static final String[] labels = {"item 1", "item 2", "item 3"};

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_calendar, container, false);

        preferences = mActivity.getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = preferences.edit();
        //Log.i("Dateeecf",preferences.getString("day1",""));

        Calendar current=Calendar.getInstance();

        int dayi=current.get(Calendar.DAY_OF_MONTH);
        String dayii = "00";
        if(dayi<10){
            dayii="0"+Integer.toString(dayi);
        }
        else{
            dayii=Integer.toString(dayi);
        }
        int monthi=current.get(Calendar.MONTH)+1;
        String monthii = "00";
        if(monthi<10){
            monthii="0"+Integer.toString(monthi);
        }
        else{
            monthii=Integer.toString(monthi);
        }
        selectedDate=dayii+"-"+monthii+"-"+Integer.toString(current.get(Calendar.YEAR));


        acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        database=FirebaseDatabase.getInstance();
        ref=database.getReference("Events");
        events=new Events();

        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list=new ArrayList<Events>();
        recyclerView.setAdapter(new ProgrammingAdapter(getContext(),list,selectedDate,mActivity));
        readRef= database.getReference().child("Events").child(acct.getId()).child(selectedDate);
        Log.i("jjjjbbbb",readRef.toString());
        readRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //float factor=0;
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Events p=dataSnapshot1.getValue(Events.class);
                    list.add(p);
                    /*if(selectedDate.equals(preferences.getString("day1",""))){
                        int temp=1;
                        if (!p.getEndEventTime().isEmpty()) {
                            String[] e = p.getEndEventTime().split(":");
                            String[] s = p.getStartEventTime().split(":");
                            temp = (Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1])) - (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                            //Log.i("Dateeecf",String.valueOf(temp*1.0/60));
                        }
                        factor+=temp*1.0/60;
                    }
                 */
                }
                recyclerView.setAdapter(new ProgrammingAdapter(getContext(),list,selectedDate,mActivity));
                refreshRecycler(recyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"Something Went Wrong!",Toast.LENGTH_LONG).show();
                refreshRecycler(recyclerView);
            }
        });


        floatingActionButton=view.findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){

                spinner = (Spinner)mdialog.findViewById(R.id.spinner1);

                //spinner.setOnItemSelectedListener(mActivity);



                eventHeading=mdialog.findViewById(R.id.event_heading);
                eventDatePicker=mdialog.findViewById(R.id.event_date);
                Date today = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                long minDate = c.getTime().getTime();
                eventDatePicker.setMinDate(minDate);
                c.add( Calendar.MONTH, 12 );
                minDate = c.getTime().getTime();
                eventDatePicker.setMaxDate(minDate);
                startTimePicker=mdialog.findViewById(R.id.start_time);
                endTimePicker=mdialog.findViewById(R.id.end_time);
                startTimeTV=mdialog.findViewById(R.id.start_timeTV);
                endTimeTV=mdialog.findViewById(R.id.end_timeTV);
                aSwitch=mdialog.findViewById(R.id.switch_en);
                aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //Log.i("gggg","gone");
                        if(isChecked){
                            startTimePicker.setVisibility(View.GONE);
                            endTimePicker.setVisibility(View.GONE);
                            startTimeTV.setVisibility(View.GONE);
                            endTimeTV.setVisibility(View.GONE);
                            Checker=1;
                            //Log.i("gggg","gone");
                        }
                        else{
                            startTimePicker.setVisibility(View.VISIBLE);
                            endTimePicker.setVisibility(View.VISIBLE);
                            startTimeTV.setVisibility(View.VISIBLE);
                            endTimeTV.setVisibility(View.VISIBLE);
                            Checker=0;
                        }
                    }
                });
                starSwitch=mdialog.findViewById(R.id.switch_star);
                events.setStar(false);
                starSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                            events.setStar(true);
                        else
                            events.setStar(false);
                    }
                });

                Button addEventButton = mdialog.findViewById(R.id.add_event_btn);
                addEventButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        events.setLabel(spinner.getSelectedItem().toString());
                        String eventHeadingCheck=eventHeading.getText().toString();
                        if(eventHeadingCheck.isEmpty()) {
                            eventHeadingCheck = "Untitled";
                        }
                        events.setHeading(eventHeadingCheck);
                        String formattedTime = "";
                        int hour = startTimePicker.getHour();
                        String sHour = "00";
                        if(hour < 10){
                            sHour = "0"+hour;
                        } else {
                            sHour = String.valueOf(hour);
                        }

                        int minute = startTimePicker.getCurrentMinute();
                        String sMinute = "00";
                        if(minute < 10){
                            sMinute = "0"+minute;
                        } else {
                            sMinute = String.valueOf(minute);
                        }

                        formattedTime = sHour+":"+sMinute;
                        if(Checker==0)
                            events.setStartEventTime(formattedTime);
                        else
                            events.setStartEventTime("");

                        hour = endTimePicker.getHour();
                        sHour = "00";
                        if(hour < 10){
                            sHour = "0"+hour;
                        } else {
                            sHour = String.valueOf(hour);
                        }

                        int minut = endTimePicker.getCurrentMinute();
                        sMinute = "00";
                        if(minut < 10){
                            sMinute = "0"+minut;
                        } else {
                            sMinute = String.valueOf(minut);
                        }

                        String formattedTimee = sHour+":"+sMinute;
                        if(Checker==0){
                            if(formattedTime.compareTo(formattedTimee)>0){
                                //Log.i("gggg","gone");
                                formattedTimee=formattedTime;
                            }
                            events.setEndEventTime(formattedTimee);}
                        else
                            events.setEndEventTime("");

                        String day = "00";
                        int d=eventDatePicker.getDayOfMonth();
                        if(d<10){
                            day="0"+Integer.toString(d);
                        }
                        else{
                            day=Integer.toString(d);
                        }

                        String month = "00";
                        int m=eventDatePicker.getMonth()+1;
                        if(m<10){
                            month="0"+Integer.toString(m);
                        }
                        else{
                            month=Integer.toString(m);
                        }

                        String year = Integer.toString(eventDatePicker.getYear());
                        String formattedDate=day+"-"+month+"-"+year;

                        ref.child(acct.getId()).child(formattedDate).child(acct.getId()+formattedTime+formattedTimee).setValue(events);
                        Toast.makeText(mContext,"Event Added",Toast.LENGTH_LONG).show();
                        mdialog.dismiss();
                        mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CalendarFragment()).commit();


                    }
                });
                mdialog.show();
            }
        });


        mdialog = new Dialog(getActivity());
        mdialog.setContentView(R.layout.popup_add);
        mdialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, 0);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 12);
        horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .defaultSelectedDate(Calendar.getInstance())
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                int days=date.get(Calendar.DAY_OF_MONTH);
                String dayss = "00";
                if(days<10){
                    dayss="0"+Integer.toString(days);
                }
                else{
                    dayss=Integer.toString(days);
                }
                int months=date.get(Calendar.MONTH)+1;
                String monthss = "00";
                if(months<10){
                    monthss="0"+Integer.toString(months);
                }
                else{
                    monthss=Integer.toString(months);
                }
                selectedDate=dayss+"-"+monthss+"-"+Integer.toString(date.get(Calendar.YEAR));

 /*               String t=preferences.getString("DaysCounter","");
                //Log.i("Counterss2",t);
                if(selectedDate.equals(preferences.getString("day2",""))) {
                    if (!t.contains("2"))
                        editor.putString("DaysCounter", t + "2");
                }
                if(selectedDate.equals(preferences.getString("day3",""))){
                    Log.i("Counterss3",t);
                    if(!t.contains("3"))
                        editor.putString("DaysCounter",t+"3");
                }
                if(selectedDate.equals(preferences.getString("day4",""))) {
                    if (!t.contains("4"))
                        editor.putString("DaysCounter", t + "4");
                }
                if(selectedDate.equals(preferences.getString("day5",""))) {
                    if (!t.contains("5"))
                        editor.putString("DaysCounter", t + "5");
                }
                if(selectedDate.equals(preferences.getString("day6",""))) {
                    if (!t.contains("6"))
                        editor.putString("DaysCounter", t + "6");
                }
                if(selectedDate.equals(preferences.getString("day7",""))) {
                    if (!t.contains("7"))
                        editor.putString("DaysCounter", t + "7");
                }

  */

                Toast.makeText(getContext(),selectedDate,Toast.LENGTH_SHORT).show();
                list=new ArrayList<Events>();
                readRef= database.getReference().child("Events").child(acct.getId()).child(selectedDate);
                //Log.i("jjjjbbbb",readRef.toString());
                readRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //float factor=0;
                        //String f="";
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            Events p=dataSnapshot1.getValue(Events.class);
                            list.add(p);
                            /*if(selectedDate.equals(preferences.getString("day1",""))){
                                int temp=1;
                                if (!p.getEndEventTime().isEmpty()) {
                                    String[] e = p.getEndEventTime().split(":");
                                    String[] s = p.getStartEventTime().split(":");
                                    temp = (Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1])) - (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                                    //Log.i("Dateeecf",String.valueOf(temp*1.0/60));
                                }
                                factor+=temp*1.0/60;
                                f="1";
                            }

                         */

                        }
                        recyclerView.setAdapter(new ProgrammingAdapter(getContext(),list,selectedDate,mActivity));
                        //Log.i("jjjjbbbb","reached2");
                        refreshRecycler(recyclerView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(),"Something Went Wrong!",Toast.LENGTH_LONG).show();
                        refreshRecycler(recyclerView);
                    }
                });
            }
        });
        return view;
    }

    private void refreshRecycler(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_anim_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
        mActivity=(AppCompatActivity)mContext;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
