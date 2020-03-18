package com.example.regular;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    Context context;
    ArrayList<Notes> notesAda;
    AppCompatActivity mActivity;
    FirebaseDatabase database;
    DatabaseReference ref;
    GoogleSignInAccount acct;
    Dialog mdialog;
    EditText noteHeading,noteText;
    Button saveNotesButton;
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
        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        database= FirebaseDatabase.getInstance();
        ref=database.getReference("Notes");
        acct = GoogleSignIn.getLastSignedInAccount(context);

        holder.headingTV.setText(notesAda.get(position).getHeading());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Title")
                        .setMessage("Do you really want to delete this event?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
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
                noteText.setText(notesAda.get(position).getText());
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

