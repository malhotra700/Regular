package com.example.regular;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NextSevenDaysFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NextSevenDaysFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NextSevenDaysFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7;
    Button checkBusyBtn;
    int s1,s2,s3,s4,s5,s6,s7;
    ArrayList<Events> list1,list2,list3,list4,list5,list6,list7;
    FirebaseDatabase database;
    DatabaseReference ref;
    GoogleSignInAccount acct;
    SharedPreferences preferences;

    private OnFragmentInteractionListener mListener;

    public NextSevenDaysFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NextSevenDaysFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NextSevenDaysFragment newInstance(String param1, String param2) {
        NextSevenDaysFragment fragment = new NextSevenDaysFragment();
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
        View view = inflater.inflate(R.layout.fragment_next_seven_days, container, false);

        database = FirebaseDatabase.getInstance();
        acct = GoogleSignIn.getLastSignedInAccount(getContext());
        ref = database.getReference("Events").child(acct.getId());
        preferences = getActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        tv1 = (TextView) view.findViewById(R.id.seven_day1);
        tv2 = (TextView) view.findViewById(R.id.seven_day2);
        tv3 = (TextView) view.findViewById(R.id.seven_day3);
        tv4 = (TextView) view.findViewById(R.id.seven_day4);
        tv5 = (TextView) view.findViewById(R.id.seven_day5);
        tv6 = (TextView) view.findViewById(R.id.seven_day6);
        tv7 = (TextView) view.findViewById(R.id.seven_day7);
        checkBusyBtn=view.findViewById(R.id.check_busy_btn);
        list1=new ArrayList<Events>();
        list2=new ArrayList<Events>();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        tv1.setText("Today");

        cal.add(Calendar.DATE, 1);
        tv2.setText(dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        tv3.setText(dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        tv4.setText(dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        tv5.setText(dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        tv6.setText(dateFormat.format(cal.getTime()));

        cal.add(Calendar.DATE, 1);
        tv7.setText(dateFormat.format(cal.getTime()));

        checkBusyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preferences.getFloat("factor1", (float) 0.0)>0)
                    tv1.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                if(preferences.getFloat("factor2", (float) 0.0)>0)
                    tv2.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                if(preferences.getFloat("factor3", (float) 0.0)>0)
                    tv3.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                if(preferences.getFloat("factor4", (float) 0.0)>0)
                    tv4.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                if(preferences.getFloat("factor5", (float) 0.0)>0)
                    tv5.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                if(preferences.getFloat("factor6", (float) 0.0)>0)
                    tv6.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                if(preferences.getFloat("factor7", (float) 0.0)>0)
                    tv7.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
            }
        });

        return view;
    }

    public void setHex(TextView tv, int s){
        if(s>0){
            tv.setBackgroundColor(this.getResources().getColor(R.color.cherry_tomato));
            Log.i("Dateee",Integer.toString(s)+"abcd");
        }
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
