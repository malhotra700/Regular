package com.example.regular;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProgressFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private AppCompatActivity mActivity;
    TextView badgesEarned,dailyStreak,counter1000,counter100,counter10,counter1,minutesMeditating;
    ImageView currentBadge1,currentBadge2,currentBadge3;
    SharedPreferences preferences;
    Dialog mdialog;
    ImageButton infoBtn;
    FirebaseDatabase database;
    DatabaseReference readRef;
    GoogleSignInAccount acct;

    private OnFragmentInteractionListener mListener;

    public ProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProgressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgressFragment newInstance(String param1, String param2) {
        ProgressFragment fragment = new ProgressFragment();
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
        View view= inflater.inflate(R.layout.fragment_progress, container, false);

        preferences = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        currentBadge1=view.findViewById(R.id.current_badge1);
        currentBadge2=view.findViewById(R.id.current_badge2);
        currentBadge3=view.findViewById(R.id.current_badge3);
        badgesEarned=view.findViewById(R.id.badges_earned);
        dailyStreak=view.findViewById(R.id.daily_streak);
        counter1000=view.findViewById(R.id.counter_1000);
        counter100=view.findViewById(R.id.counter_100);
        counter10=view.findViewById(R.id.counter_10);
        counter1=view.findViewById(R.id.counter_1);
        minutesMeditating=view.findViewById(R.id.minutes_meditating);
        infoBtn=view.findViewById(R.id.badges_info_btn);
        mdialog = new Dialog(getActivity());
        mdialog.setContentView(R.layout.popup_badge_info);
        mdialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        mdialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.show();
            }
        });

        database=FirebaseDatabase.getInstance();
        acct = GoogleSignIn.getLastSignedInAccount(mActivity);
        readRef=database.getReference("Progress");

        String a=preferences.getInt("DailyStreak",0) +"";
        readRef.child(acct.getId()+"DailyStreak").setValue(Integer.parseInt(a));
        dailyStreak.setText(a);
        int av;
        int b=av=preferences.getInt("AppVisits",0);
        readRef.child(acct.getId()+"AppVisits").setValue(b);
        a=b/1000 +"";
        counter1000.setText(a);
        b=b%1000;
        a=b/100 +"";
        counter100.setText(a);
        b=b%100;
        a=b/10 +"";
        counter10.setText(a);
        b=b%10;
        a=b +"";
        counter1.setText(a);
        a=preferences.getInt("MinutesMeditating",0) +"";
        readRef.child(acct.getId()+"MinutesMeditating").setValue(Integer.parseInt(a));
        minutesMeditating.setText(a);
        int ds=Integer.parseInt(dailyStreak.getText().toString());
        int total=2;
        if(ds>=1 && ds<5) {
            currentBadge1.setImageResource(R.drawable.badge_as_easy_as_1);
            total+=1;
        }
        else if(ds>=5 && ds<25) {
            currentBadge1.setImageResource(R.drawable.ic_take);
            total+=2;
        }
        else if(ds>=25 && ds<125) {
            currentBadge1.setImageResource(R.drawable.badge_determined_25);
            total+=3;
        }
        else if(ds>=125 && ds<625) {
            currentBadge1.setImageResource(R.drawable.badge_commited_125);
            total+=4;
        }
        else if(ds>=625) {
            currentBadge1.setImageResource(R.drawable.badge_dedicated_625);
            total+=5;
        }

        if(av>=50 && av<250) {
            currentBadge2.setImageResource(R.drawable.badge_daring_50);
            total+=1;
        }
        else if(av>=250 && av<1000) {
            currentBadge2.setImageResource(R.drawable.badge_ultimate_250);
            total+=2;
        }
        else if(av>=1000) {
            currentBadge2.setImageResource(R.drawable.ic_finish);
            total+=3;
        }

        ds=Integer.parseInt(minutesMeditating.getText().toString());
        if(ds>=60 && ds<480) {
            currentBadge3.setImageResource(R.drawable.badge_early_60);
            total += 1;
        }
        else if(ds>=480 && ds<2400) {
            currentBadge3.setImageResource(R.drawable.badge_zen_480);
            total += 2;
        }
        else if(ds>=2400) {
            currentBadge3.setImageResource(R.drawable.badge_saint_2400);
            total += 3;
        }
        badgesEarned.setText(total+"/13");


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
