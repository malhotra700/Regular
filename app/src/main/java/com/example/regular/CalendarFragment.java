package com.example.regular;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;


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
    Dialog mdialog;
    FloatingActionButton floatingActionButton;
    EditText eventHeading;
    DatePicker eventDatePicker;
    TimePicker startTimePicker,endTimePicker;
    Events events;

    FirebaseDatabase database;
    DatabaseReference ref;
    GoogleSignInAccount acct;

    private Context mContext;
    private AppCompatActivity mActivity;

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

        acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        database=FirebaseDatabase.getInstance();
        ref=database.getReference("Events");
        events=new Events();

        floatingActionButton=view.findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                Button addEventButton = mdialog.findViewById(R.id.add_event_btn);
                addEventButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        eventHeading=mdialog.findViewById(R.id.event_heading);
                        eventDatePicker=mdialog.findViewById(R.id.event_date);
                        startTimePicker=mdialog.findViewById(R.id.start_time);
                        endTimePicker=mdialog.findViewById(R.id.end_time);
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
                        events.setStartEventTime(formattedTime);

                        hour = endTimePicker.getHour();
                        sHour = "00";
                        if(hour < 10){
                            sHour = "0"+hour;
                        } else {
                            sHour = String.valueOf(hour);
                        }

                        minute = startTimePicker.getCurrentMinute();
                        sMinute = "00";
                        if(minute < 10){
                            sMinute = "0"+minute;
                        } else {
                            sMinute = String.valueOf(minute);
                        }

                        formattedTime = sHour+":"+sMinute;
                        events.setEndEventTime(formattedTime);

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
                        formattedTime=day+"-"+month+"-"+year;

                        ref.child(acct.getId()).child(formattedTime).child(eventHeading.getText().toString()).setValue(events);
                        Toast.makeText(mContext,"Event Added",Toast.LENGTH_LONG).show();
                        mdialog.dismiss();


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
                .addEvents(new CalendarEventsPredicate() {
                    @Override
                    public List<CalendarEvent> events(Calendar date) {
                        List<CalendarEvent> events = new ArrayList<>();
                        events.add(new CalendarEvent(Color.rgb(10, 100, 50), "event X"));
                        return events;
                    }
                })
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                Log.i("Info",date.toString());
                Toast.makeText(getContext(),Integer.toString(date.get(Calendar.DAY_OF_MONTH))
                        +" "+Integer.toString(date.get(Calendar.MONTH)+1)+" "+
                        Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_LONG).show();
            }
        });
        return view;
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
