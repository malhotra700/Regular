package com.example.regular;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.regular.HomeActivity.*;

public class ProgrammingAdapter extends RecyclerView.Adapter<ProgrammingAdapter.ProgrammingViewHolder> {
    Context context;
    ArrayList<Events> eventsAda;
    DatabaseReference ref;
    FirebaseDatabase database;
    GoogleSignInAccount acct;
    SharedPreferences prefs;
    String selected;
    AppCompatActivity mActivity;

    public  ProgrammingAdapter(Context c,ArrayList<Events> e,String selected,AppCompatActivity mActivity){
        this.context=c;
        this.eventsAda=e;
        this.selected=selected;
        this.mActivity=mActivity;
    }
    @NonNull
    @Override
    public ProgrammingAdapter.ProgrammingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProgrammingViewHolder(LayoutInflater.from(context).inflate(R.layout.item_event,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProgrammingAdapter.ProgrammingViewHolder holder, final int position) {
        String checkLabel=eventsAda.get(position).getLabel();
        if(checkLabel.equals("Meet/Schedule")){
            holder.imageView.setImageResource(R.drawable.ic_meet);
            checkLabel="Meeting";
        }
        else if(checkLabel.equals("Call"))
            holder.imageView.setImageResource(R.drawable.ic_call);
        else if(checkLabel.equals("Email"))
            holder.imageView.setImageResource(R.drawable.ic_email);
        else if(checkLabel.equals("Pay"))
            holder.imageView.setImageResource(R.drawable.ic_pay);
        else if(checkLabel.equals("Deadline"))
            holder.imageView.setImageResource(R.drawable.ic_deadline);
        else if(checkLabel.equals("Study"))
            holder.imageView.setImageResource(R.drawable.ic_study);
        else if(checkLabel.equals("Exercise"))
            holder.imageView.setImageResource(R.drawable.ic_exercise);
        else if(checkLabel.equals("Get"))
            holder.imageView.setImageResource(R.drawable.ic_get);
        else if(checkLabel.equals("Take"))
            holder.imageView.setImageResource(R.drawable.ic_take);
        else if(checkLabel.equals("Check"))
            holder.imageView.setImageResource(R.drawable.ic_check);
        else if(checkLabel.equals("Do"))
            holder.imageView.setImageResource(R.drawable.ic_do);
        else if(checkLabel.equals("Pick"))
            holder.imageView.setImageResource(R.drawable.ic_pick);
        else if(checkLabel.equals("Finish"))
            holder.imageView.setImageResource(R.drawable.ic_finish);
        else if(checkLabel.equals("Buy"))
            holder.imageView.setImageResource(R.drawable.ic_buy);
        else if(checkLabel.equals("Clean"))
            holder.imageView.setImageResource(R.drawable.ic_clean);
        else if(checkLabel.equals("Print"))
            holder.imageView.setImageResource(R.drawable.ic_print);
        else if(checkLabel.equals("Read"))
            holder.imageView.setImageResource(R.drawable.ic_read);
        else if(checkLabel.equals("Other"))
            holder.imageView.setImageResource(R.drawable.ic_other);
        holder.labelTV.setText(checkLabel);
        if(eventsAda.get(position).getStar())
            holder.starView.setVisibility(View.VISIBLE);
        holder.headingTV.setText(eventsAda.get(position).getHeading());
        holder.startTV.setText(eventsAda.get(position).getStartEventTime());
        holder.endTV.setText(eventsAda.get(position).getEndEventTime());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("position",eventsAda.get(position).getHeading());
                new AlertDialog.Builder(context)
                        .setTitle("Title")
                        .setMessage("Do you really want to delete this event?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                database=FirebaseDatabase.getInstance();
                                ref=database.getReference("Events");
                                acct = GoogleSignIn.getLastSignedInAccount(context);

                                Log.i("position",selected);
                                ref.child(acct.getId()).child(selected).child(acct.getId()+eventsAda.get(position).getStartEventTime()+eventsAda.get(position).getEndEventTime()).removeValue();
                                mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.l_layout,new EventsFragment()).commit();
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        });
        //setAnimation(holder.itemView, position);
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (true)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);

        }
    }

    @Override
    public int getItemCount() {
        return eventsAda.size();
    }
    @Override
    public long getItemId(int id) {
        return id;
    }

    public class ProgrammingViewHolder extends RecyclerView.ViewHolder{
        TextView headingTV,startTV,endTV,labelTV;
        ImageView imageView,starView;
        LinearLayout linearLayout;

        public ProgrammingViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.item_linear);
            imageView=(ImageView)itemView.findViewById(R.id.item_icon);
            starView=(ImageView)itemView.findViewById(R.id.item_star);
            labelTV=(TextView) itemView.findViewById(R.id.item_label);
            headingTV=(TextView) itemView.findViewById(R.id.item_heading);
            startTV=(TextView)itemView.findViewById(R.id.item_start_time);
            endTV=(TextView)itemView.findViewById(R.id.item_end_time);

        }
    }
}
