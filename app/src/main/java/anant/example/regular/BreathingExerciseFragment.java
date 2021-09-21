package anant.example.regular;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BreathingExerciseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BreathingExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BreathingExerciseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private AppCompatActivity mActivity;

    private OnFragmentInteractionListener mListener;
    Button stopBtn,saveSettingsBtn;
    ImageButton chooseTechniqueBtn;
    String currentState;
    TextView breathingTV;
    View breatheView,bigBreatheView;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Switch breathingSwitch;
    EditText timerDuration;
    Chronometer stopwatchTV;
    Dialog mdialog;
    SharedPreferences preferences;
    TextToSpeech tts;

    FirebaseDatabase database;
    DatabaseReference readRef;
    GoogleSignInAccount acct;

    public BreathingExerciseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BreathingExerciseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BreathingExerciseFragment newInstance(String param1, String param2) {
        BreathingExerciseFragment fragment = new BreathingExerciseFragment();
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
        View view= inflater.inflate(R.layout.fragment_breathing_exercise, container, false);

        preferences = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        stopBtn=view.findViewById(R.id.stop_btn);
        stopwatchTV=view.findViewById(R.id.stopwatch_time);
        breatheView=view.findViewById(R.id.breathe_view);
        bigBreatheView=view.findViewById(R.id.big_breathe_view);
        breathingTV=view.findViewById(R.id.breathing_tv);
        breathingTV.setText(preferences.getString("BreathingTechnique",""));
        currentState="START";
        mdialog = new Dialog(getActivity());
        mdialog.setContentView(R.layout.popup_choose_technique);
        mdialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        mdialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        chooseTechniqueBtn=view.findViewById(R.id.choose_technique_btn);
        chooseTechniqueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup=mdialog.findViewById(R.id.groupradio);
                String check=preferences.getString("BreathingTechnique","");
                if(!check.isEmpty()) {
                    if(check.equals("Early Morning"))
                        radioButton=(RadioButton)radioGroup.findViewById(R.id.radia_id1);
                    else if(check.equals("Deep Breathing"))
                        radioButton=(RadioButton)radioGroup.findViewById(R.id.radia_id2);
                    else if(check.equals("Pranayama"))
                        radioButton=(RadioButton)radioGroup.findViewById(R.id.radia_id3);
                    else if(check.equals("Box Breathing"))
                        radioButton=(RadioButton)radioGroup.findViewById(R.id.radia_id4);
                    radioButton.setChecked(true);
                }
                breathingSwitch=mdialog.findViewById(R.id.switch_breathing);
                if(preferences.getBoolean("GuidedBreathing",true))
                    breathingSwitch.setChecked(preferences.getBoolean("GuidedBreathing",true));
                timerDuration=mdialog.findViewById(R.id.timer_duration);
                if(preferences.getInt("TimerDuration",-1)!=-1) {
                    int x=preferences.getInt("TimerDuration",5);
                    timerDuration.setText(Integer.toString(x));
                }
                saveSettingsBtn=mdialog.findViewById(R.id.save_settings_btn);
                saveSettingsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioButton=(RadioButton)radioGroup.findViewById(selectedId);
                        editor.putString("BreathingTechnique",radioButton.getText().toString());
                        String x=timerDuration.getText().toString();
                        if(!x.isEmpty() && !(x.charAt(0) == '0'))
                            editor.putInt("TimerDuration",Integer.parseInt(x));
                        editor.putBoolean("GuidedBreathing",breathingSwitch.isChecked());
                        editor.apply();
                        mdialog.dismiss();
                        mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.l_layout,new HealthFragment()).commit();
                    }
                });

                mdialog.show();
            }
        });



        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentState.equals("START")) {
                    stopBtn.setText("STOP");
                    bigBreatheView.setVisibility(View.VISIBLE);
                    stopwatchTV.setBase(SystemClock.elapsedRealtime());
                    currentState="STOP";
                    String currtech=preferences.getString("BreathingTechnique","");
                    if(currtech.equals("Early Morning"))
                        scaleView1(breatheView,"Breathe In","Breathe Out",1f,1.5f,6000,0,2000,0);
                    else if(currtech.equals("Deep Breathing"))
                        scaleView1(breatheView,"Breathe In","Breathe Out",1f,1.5f,4000,7000,8000,0);
                    else if(currtech.equals("Pranayama"))
                        scaleView1(breatheView,"Breathe In","Breathe Out",1f,1.5f,7000,4000,8000,4000);
                    else if(currtech.equals("Box Breathing"))
                        scaleView1(breatheView,"Breathe In","Breathe Out",1f,1.5f,4000,4000,4000,4000);
                    stopwatchTV.start();
                    stopwatchTV.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                        @Override
                        public void onChronometerTick(Chronometer chronometer) {
                            int x=preferences.getInt("TimerDuration",5);
                            String temp;
                            if (x<10)
                                temp="0"+x+":00:0";
                            else
                                temp=x+":00:0";
                            //Log.i("tempValue",temp+"="+chronometer.getText().toString());
                            if( chronometer.getText().toString().equals(temp)) {
                                //Toast.makeText(mContext, "Time duration Completed", Toast.LENGTH_SHORT).show();
                                //stopBtn.performClick();
                                //stopBtn.setText("START");
                                Log.i("tempValue","a");
                                bigBreatheView.setVisibility(View.GONE);
                                Log.i("tempValue","a1");
                                //currentState="START";
                                Log.i("tempValue","a2");
                                breathingTV.setText("Time duration Completed");
                                Log.i("tempValue","a3");
                                scaleView(breatheView,1.5f,1f);
                                Log.i("tempValue","a4");
                                stopwatchTV.stop();
                                Log.i("tempValue",currentState);
                                //stopwatchTV.setBase(SystemClock.elapsedRealtime());
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(mContext, "Stopped", Toast.LENGTH_SHORT).show();
                    stopBtn.setText("START");
                    bigBreatheView.setVisibility(View.GONE);
                    currentState="START";
                    breathingTV.setText(preferences.getString("BreathingTechnique",""));
                    scaleView(breatheView,1.5f,1f);
                    String[] mm=stopwatchTV.getText().toString().split(":");
                    int i=preferences.getInt("MinutesMeditating",0);
                    editor.putInt("MinutesMeditating",Integer.parseInt(mm[0])+i);
                    editor.apply();
                    stopwatchTV.stop();
                    stopwatchTV.setBase(SystemClock.elapsedRealtime());
                }
            }
        });
        return view;
    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, .5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, .5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(1000);
        v.startAnimation(anim);
    }

    public void scaleView1(final View v, final String breatheState1, final String breatheState2, final float startScale, final float endScale,
                           final int inhaleDuration, final int inhaleHold,
                           final int exhaleDuration, final int exhaleHold) {
        final Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, .5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, .5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(inhaleDuration);
        v.startAnimation(anim);
        breathingTV.setText(breatheState1);
        if(preferences.getBoolean("GuidedBreathing",true)) {
            tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = tts.setLanguage(Locale.US);
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "This Language is not supported");
                        }
                        speak(breatheState1);

                    } else {
                        Log.e("TTS", "Initilization Failed!");
                    }
                }
            });
        }
        anim.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        breathingTV.setText(breatheState2);
                        scaleView1(v,breatheState2,breatheState1,endScale,startScale,exhaleDuration,exhaleHold,inhaleDuration,inhaleHold);
                    }
                }, inhaleHold);
            }
        });
    }

    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
