package yogispark.chat.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import yogispark.chat.Models.Message;
import yogispark.chat.R;
import yogispark.chat.ViewHolder.ChatViewHolder;

/**
 * Created by yogesh on 5/10/16.
 */
public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    ArrayList<Message> Messages;
    public ChatViewAdapter(ArrayList<Message> Messages){
        this.Messages = Messages;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_cardview,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.getContactName().setText(Messages.get(position).Contact_Name);
        holder.getContactStatus().setText(Messages.get(position).Contact_Status);
        holder.getMessage().setText(Messages.get(position).Body);
        holder.getPostedDate().setText(Messages.get(position).PostedTime ==null ? "" : Messages.get(position).PostedTime.substring(0,11));

        if(Messages.get(position).PostedTime!=null){
            holder.getPostedDate().setTextColor(Color.RED);
        }else{
            holder.getPostedDate().setTextColor(Color.BLACK);
        }
        if(Messages.get(position).DeliveryTime!=null){
            holder.getPostedDate().setTextColor(Color.GREEN);
        }else{
            holder.getPostedDate().setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return Messages.size();
    }
}
