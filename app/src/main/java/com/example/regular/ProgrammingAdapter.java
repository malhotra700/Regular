package com.example.regular;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProgrammingAdapter extends RecyclerView.Adapter<ProgrammingAdapter.ProgrammingViewHolder> {
    Context context;
    ArrayList<Events> eventsAda;

    public  ProgrammingAdapter(Context c,ArrayList<Events> e){
        this.context=c;
        this.eventsAda=e;
    }
    @NonNull
    @Override
    public ProgrammingAdapter.ProgrammingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProgrammingViewHolder(LayoutInflater.from(context).inflate(R.layout.item_event,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProgrammingAdapter.ProgrammingViewHolder holder, int position) {
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
        holder.headingTV.setText(eventsAda.get(position).getHeading());
        holder.startTV.setText(eventsAda.get(position).getStartEventTime());
        holder.endTV.setText(eventsAda.get(position).getEndEventTime());
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
        ImageView imageView;

        public ProgrammingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.item_icon);
            labelTV=(TextView) itemView.findViewById(R.id.item_label);
            headingTV=(TextView) itemView.findViewById(R.id.item_heading);
            startTV=(TextView)itemView.findViewById(R.id.item_start_time);
            endTV=(TextView)itemView.findViewById(R.id.item_end_time);

        }
    }
}
