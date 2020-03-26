package com.example.regular;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private AppCompatActivity mActivity;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ArrayList<Notes> list;
    FirebaseDatabase database;
    DatabaseReference readRef;
    Notes notes;
    EditText noteHeading,noteText;
    Button saveNotesButton,addBulletBtn;
    ImageButton shareToWhatsappBtn,shareToGmailBtn;
    LinearLayout extraBtns;
    ImageButton sttBtn;
    SpeechRecognizer mspeechRecog;
    Intent mSpeechRecogInt;
    Dialog mdialog;
    GoogleSignInAccount acct;

    private CalendarFragment.OnFragmentInteractionListener mListener;

    public TasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TasksFragment newInstance(String param1, String param2) {
        TasksFragment fragment = new TasksFragment();
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
        View view= inflater.inflate(R.layout.fragment_tasks, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.tasks_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list=new ArrayList<Notes>();
        notes=new Notes();
        database=FirebaseDatabase.getInstance();
        acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        readRef=database.getReference("Notes").child(acct.getId());
        recyclerView.setAdapter(new NotesAdapter(getContext(),list,mActivity));
        readRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    Notes p = dataSnapshot1.getValue(Notes.class);
                    list.add(p);
                }
                //Log.i("taskcheck",list.toString());
                recyclerView.setAdapter(new NotesAdapter(getContext(),list,mActivity));
                refreshRecycler(recyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mdialog = new Dialog(getActivity());
        mdialog.setContentView(R.layout.popup_note);
        mdialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        floatingActionButton=view.findViewById(R.id.fab_task);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteHeading=mdialog.findViewById(R.id.task_heading);
                extraBtns=mdialog.findViewById(R.id.extra_btns);
                noteText=mdialog.findViewById(R.id.task_text);
                noteText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            extraBtns.setVisibility(View.VISIBLE);
                        } else {
                            extraBtns.setVisibility(View.GONE);
                        }
                    }
                });
                addBulletBtn=mdialog.findViewById(R.id.task_bullet_btn);
                addBulletBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //String abc=noteText.getText().toString() + " • ";
                        //noteText.setText(abc);
                        //noteText.setSelection(noteText.length());
                        noteText.getText().insert(noteText.getSelectionStart(),  " • ");

                    }
                });

                mspeechRecog = SpeechRecognizer.createSpeechRecognizer(getContext());
                mSpeechRecogInt = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                mSpeechRecogInt.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                mSpeechRecogInt.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                mspeechRecog.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {

                    }

                    @Override
                    public void onBeginningOfSpeech() {

                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {

                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {

                    }

                    @Override
                    public void onEndOfSpeech() {

                    }

                    @Override
                    public void onError(int error) {

                    }

                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        Log.i("sttCheck",matches.toString());
                        if (matches != null) {
                            String recordstr = matches.get(0);
                            noteText.getText().insert(noteText.getSelectionStart()," "+recordstr  );
                        }

                    }

                    @Override
                    public void onPartialResults(Bundle partialResults) {

                    }

                    @Override
                    public void onEvent(int eventType, Bundle params) {

                    }
                });
                sttBtn=mdialog.findViewById(R.id.task_stt_btn);
                sttBtn.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.i("sttCheck","touched");
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_UP:
                                mspeechRecog.stopListening();
                                Log.i("sttCheck1","hello");
                                break;
                            case MotionEvent.ACTION_DOWN:
                                mspeechRecog.startListening(mSpeechRecogInt);
                                Log.i("sttCheck2","hello");
                                break;
                        }
                        return false;
                    }
                });
                shareToWhatsappBtn=mdialog.findViewById(R.id.share_to_whatsapp);
                shareToWhatsappBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, noteHeading.getText().toString()+"\n"+
                                noteText.getText().toString());
                        try {
                            mActivity.startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(mContext,"WhatsApp is not installed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                shareToGmailBtn=mdialog.findViewById(R.id.share_to_gmail);
                shareToGmailBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW)
                                .setType("plain/text")
                                .setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail")
                                .putExtra(Intent.EXTRA_SUBJECT, noteHeading.getText().toString())
                                .putExtra(Intent.EXTRA_TEXT, noteText.getText().toString());
                        try {
                            mActivity.startActivity(intent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(mContext,"Gmail is not installed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                saveNotesButton=mdialog.findViewById(R.id.add_task_btn);
                saveNotesButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        if(noteHeading.getText().toString().isEmpty())
                            notes.setHeading("Untitled "+Calendar.getInstance().getTime());
                        else
                            notes.setHeading(noteHeading.getText().toString());
                        notes.setText(noteText.getText().toString());
                        readRef.child(acct.getId()+notes.getHeading()).setValue(notes);
                        mdialog.dismiss();
                        mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.l_layout,new TasksFragment()).commit();
                    }
                });
                mdialog.show();
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
