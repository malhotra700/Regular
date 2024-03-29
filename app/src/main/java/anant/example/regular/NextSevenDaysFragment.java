package anant.example.regular;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Locale;

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

    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,busyTV,workingTV;
    Button checkBusyBtn;
    TextToSpeech tts;
    SharedPreferences preferences;
    LinearLayout linearLayout;
    FirebaseDatabase database;
    DatabaseReference readRef;
    GoogleSignInAccount acct;
    float factor1,factor2,factor3,factor4,factor5,factor6,factor7,s;

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

        acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        database=FirebaseDatabase.getInstance();
        readRef=database.getReference("Events").child(acct.getId());


        preferences = getActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        tv1 = (TextView) view.findViewById(R.id.seven_day1);
        tv2 = (TextView) view.findViewById(R.id.seven_day2);
        tv3 = (TextView) view.findViewById(R.id.seven_day3);
        tv4 = (TextView) view.findViewById(R.id.seven_day4);
        tv5 = (TextView) view.findViewById(R.id.seven_day5);
        tv6 = (TextView) view.findViewById(R.id.seven_day6);
        tv7 = (TextView) view.findViewById(R.id.seven_day7);
        checkBusyBtn=view.findViewById(R.id.check_busy_btn);
        linearLayout=view.findViewById(R.id.working_hrs);
        busyTV=view.findViewById(R.id.busy_tv);
        workingTV=view.findViewById(R.id.working_tv);
        factor1=factor2=factor3=factor4=factor5=factor6=factor7=0;

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        tv1.setText("Today");
        readRef.child(dateFormat.format(cal.getTime())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Events p=dataSnapshot1.getValue(Events.class);
                    int temp=1;
                    if (!p.getEndEventTime().isEmpty()) {
                        String[] e = p.getEndEventTime().split(":");
                        String[] s = p.getStartEventTime().split(":");
                        temp = (Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1])) - (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                        }
                        factor1+=temp*1.0/60;

                }
                Log.i("Dateeecf1",factor1+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cal.add(Calendar.DATE, 1);
        tv2.setText(dateFormat.format(cal.getTime()));
        readRef.child(dateFormat.format(cal.getTime())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Events p=dataSnapshot1.getValue(Events.class);
                    int temp=1;
                    if (!p.getEndEventTime().isEmpty()) {
                        String[] e = p.getEndEventTime().split(":");
                        String[] s = p.getStartEventTime().split(":");
                        temp = (Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1])) - (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                    }
                    factor2+=temp*1.0/60;

                }
                Log.i("Dateeecf2",factor2+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cal.add(Calendar.DATE, 1);
        tv3.setText(dateFormat.format(cal.getTime()));
        readRef.child(dateFormat.format(cal.getTime())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Events p=dataSnapshot1.getValue(Events.class);
                    int temp=1;
                    if (!p.getEndEventTime().isEmpty()) {
                        String[] e = p.getEndEventTime().split(":");
                        String[] s = p.getStartEventTime().split(":");
                        temp = (Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1])) - (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                    }
                    factor3+=temp*1.0/60;

                }
                Log.i("Dateeecf3",factor3+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cal.add(Calendar.DATE, 1);
        tv4.setText(dateFormat.format(cal.getTime()));
        readRef.child(dateFormat.format(cal.getTime())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Events p=dataSnapshot1.getValue(Events.class);
                    int temp=1;
                    if (!p.getEndEventTime().isEmpty()) {
                        String[] e = p.getEndEventTime().split(":");
                        String[] s = p.getStartEventTime().split(":");
                        temp = (Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1])) - (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                    }
                    factor4+=temp*1.0/60;

                }
                Log.i("Dateeecf4",factor4+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cal.add(Calendar.DATE, 1);
        tv5.setText(dateFormat.format(cal.getTime()));
        readRef.child(dateFormat.format(cal.getTime())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Events p=dataSnapshot1.getValue(Events.class);
                    int temp=1;
                    if (!p.getEndEventTime().isEmpty()) {
                        String[] e = p.getEndEventTime().split(":");
                        String[] s = p.getStartEventTime().split(":");
                        temp = (Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1])) - (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                    }
                    factor5+=temp*1.0/60;

                }
                Log.i("Dateeecf5",factor5+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cal.add(Calendar.DATE, 1);
        tv6.setText(dateFormat.format(cal.getTime()));
        readRef.child(dateFormat.format(cal.getTime())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Events p=dataSnapshot1.getValue(Events.class);
                    int temp=1;
                    if (!p.getEndEventTime().isEmpty()) {
                        String[] e = p.getEndEventTime().split(":");
                        String[] s = p.getStartEventTime().split(":");
                        temp = (Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1])) - (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                    }
                    factor6+=temp*1.0/60;

                }
                Log.i("Dateeecf6",factor6+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cal.add(Calendar.DATE, 1);
        tv7.setText(dateFormat.format(cal.getTime()));
        readRef.child(dateFormat.format(cal.getTime())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Events p=dataSnapshot1.getValue(Events.class);
                    int temp=1;
                    if (!p.getEndEventTime().isEmpty()) {
                        String[] e = p.getEndEventTime().split(":");
                        String[] s = p.getStartEventTime().split(":");
                        temp = (Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1])) - (Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]));
                    }
                    factor7+=temp*1.0/60;

                }
                Log.i("Dateeecf7",factor7+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkBusyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String count=preferences.getString("DaysCounter","");
                Log.i("Counterss",count);

                    linearLayout.setVisibility(View.VISIBLE);
                    workingTV.setVisibility(View.VISIBLE);
                    s=factor1;
                    if (factor1 == 0)
                        tv1.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                    if (factor1> 0 && factor1< 3.5)
                        tv1.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry1));
                    if (factor1> 3.5)
                        tv1.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry2));

                    s+=factor2;
                    if (factor2 == 0)
                        tv2.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                    if (factor2 > 0 && factor2 < 3.5)
                        tv2.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry1));
                    if (factor2 > 3.5)
                        tv2.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry2));

                    s+=factor3;
                    if (factor3 == 0)
                        tv3.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                    if (factor3 > 0 && factor3 < 3.5)
                        tv3.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry1));
                    if (factor3 > 3.5)
                        tv3.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry2));

                    s+=factor4;
                    if (factor4== 0)
                        tv4.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                    if (factor4 > 0 && factor4 < 3.5)
                        tv4.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry1));
                    if (factor4 > 3.5)
                        tv4.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry2));

                    s+=factor5;
                    if (factor5 == 0)
                        tv5.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                    if (factor5 > 0 && factor5 < 3.5)
                        tv5.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry1));
                    if (factor5> 3.5)
                        tv5.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry2));

                    s+=factor6;
                    if (factor6== 0)
                        tv6.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                    if (factor6 > 0 && factor6 < 3.5)
                        tv6.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry1));
                    if (factor6 > 3.5)
                        tv6.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry2));

                    s+=factor7;
                    if (factor7== 0)
                        tv7.setBackgroundColor(getActivity().getResources().getColor(R.color.cherry_tomato));
                    if (factor7 > 0 && factor7 < 3.5)
                        tv7.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry1));
                    if (factor7 > 3.5)
                        tv7.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_cherry2));

                    s=s/7;
                    final String text;
                    if(s==0)
                        text="Free!";
                    else if(s>0 && s<1.5)
                        text="Free! but got some work.";
                    else if(s>1.5 && s<2.5)
                        text="Busy! but can manage some work.";
                    else if(s>2.5 && s<3.5)
                        text="Busy!";
                    else
                        text="Very Busy!";
                    busyTV.setText(text);
                    busyTV.setVisibility(View.VISIBLE);

                    tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                int result = tts.setLanguage(Locale.US);
                                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                    Log.e("TTS", "This Language is not supported");
                                }
                                speak(text);

                            } else {
                                Log.e("TTS", "Initilization Failed!");
                            }
                        }
                    });

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
    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
