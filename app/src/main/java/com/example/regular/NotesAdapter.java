package com.example.regular;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    Context context;
    ArrayList<Notes> notesAda;
    AppCompatActivity mActivity;
    FirebaseDatabase database;
    DatabaseReference ref;
    GoogleSignInAccount acct;
    Dialog mdialog;
    EditText noteHeading,noteText;
    Button saveNotesButton,addBulletBtn;
    ImageButton doneBtn;
    ImageButton shareToWhatsappBtn,shareToGmailBtn;
    LinearLayout extraBtns;
    ImageButton sttBtn;
    SpeechRecognizer mspeechRecog;
    Intent mSpeechRecogInt;
    Notes noteTemp;

    public  NotesAdapter(Context c,ArrayList<Notes> n,AppCompatActivity mActivity){
        this.context=c;
        this.notesAda=n;
        this.mActivity=mActivity;
    }
    @NonNull
    @Override
    public NotesAdapter.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notes,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotesViewHolder holder, final int position) {
        mdialog = new Dialog(mActivity);
        mdialog.setContentView(R.layout.popup_note);
        mdialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        mdialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        database= FirebaseDatabase.getInstance();
        ref=database.getReference("Notes");
        acct = GoogleSignIn.getLastSignedInAccount(context);


        holder.headingTV.setText(notesAda.get(position).getHeading());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Note")
                        .setMessage("Do you really want to delete this note?")
                        .setIcon(R.drawable.ic_deadline)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                ref.child(acct.getId()).child(acct.getId()+notesAda.get(position).getHeading()).removeValue();
                                mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.l_layout,new TasksFragment()).commit();
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteHeading=mdialog.findViewById(R.id.task_heading);
                noteHeading.setText(notesAda.get(position).getHeading());
                noteText=mdialog.findViewById(R.id.task_text);
                addBulletBtn=mdialog.findViewById(R.id.task_bullet_btn);
                doneBtn=mdialog.findViewById(R.id.task_done_btn);
                sttBtn=mdialog.findViewById(R.id.task_stt_btn);
                noteText=mdialog.findViewById(R.id.task_text);
                noteText.setText(notesAda.get(position).getText());
                noteText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            addBulletBtn.setVisibility(View.VISIBLE);
                            sttBtn.setVisibility(View.VISIBLE);
                            doneBtn.setVisibility(View.VISIBLE);
                        } else {
                            addBulletBtn.setVisibility(View.GONE);
                            sttBtn.setVisibility(View.GONE);
                            doneBtn.setVisibility(View.GONE);
                        }
                    }
                });
                addBulletBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noteText.getText().insert(noteText.getSelectionStart(),  " • ");

                    }
                });
                doneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i=noteText.getSelectionStart(),j;
                        char a=' ';
                        String b=noteText.getText().toString().substring(0,i);
                        for(j=i-1;j>=0;j--){
                            if(b.charAt(j)=='•') {
                                a='•';
                                break;
                            }
                            if(b.charAt(j)=='✓') {
                                a='✓';
                                break;
                            }
                        }
                        if(a!=' ' && a=='✓')
                            noteText.getText().replace(j,j+1,"•");
                        if(a!=' ' && a=='•')
                            noteText.getText().replace(j,j+1,"✓");
                    }
                });

                mspeechRecog = SpeechRecognizer.createSpeechRecognizer(context);
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
                            Toast.makeText(context,"WhatsApp is not installed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                shareToGmailBtn=mdialog.findViewById(R.id.share_to_gmail);
                shareToGmailBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, noteHeading.getText().toString());
                        emailIntent.putExtra(Intent.EXTRA_TEXT,noteText.getText().toString());
                        context.startActivity(Intent.createChooser(emailIntent, null));
                    }
                });
                saveNotesButton=mdialog.findViewById(R.id.add_task_btn);
                saveNotesButton.setText("Update Note");
                saveNotesButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        noteTemp=new Notes();
                        noteTemp.setHeading(noteHeading.getText().toString());
                        noteTemp.setText(noteText.getText().toString());
                        ref.child(acct.getId()).child(acct.getId()+notesAda.get(position).getHeading()).removeValue();
                        ref.child(acct.getId()).child(acct.getId()+noteTemp.getHeading()).setValue(noteTemp);
                        mdialog.dismiss();
                        mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.l_layout,new TasksFragment()).commit();
                    }
                });
                mdialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return notesAda.size();
    }
    @Override
    public long getItemId(int id) {
        return id;
    }


    public class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView headingTV;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            headingTV=itemView.findViewById(R.id.note_heading);

        }
    }
}

