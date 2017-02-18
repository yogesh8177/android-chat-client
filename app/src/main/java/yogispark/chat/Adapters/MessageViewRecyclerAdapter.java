package yogispark.chat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;

import yogispark.chat.Models.Message;
import yogispark.chat.Models.MessageView;
import yogispark.chat.R;
import yogispark.chat.UI.ChatView;
import yogispark.chat.Utility.Constants;
import yogispark.chat.Utility.Tools;
import yogispark.chat.ViewHolder.MessageViewHolder;

/**
 * Created by yogesh on 5/10/16.
 */
public class MessageViewRecyclerAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    ArrayList<MessageView> Data;
    Context context;

    public MessageViewRecyclerAdapter(Context context, ArrayList<MessageView> Data){
        this.context = context;
        this.Data = Data;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view_cardview,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder,final int position) {
        //holder.getContactImage().setImageDrawable();
        holder.getContactName().setText(Data.get(position).Contact_Name);
        holder.getLatestMessage().setText(Data.get(position).Body);
        holder.getCount().setText(String.valueOf(Data.get(position).Count));

        Date date = Tools.getGMTDate(Data.get(position).Posted_Date);
        String dateTime = date != null ? Tools.geyDateTimePart(date) : "";
        holder.getPostedDate().setText(dateTime);

        if(Data.get(position).Category == Constants.CATEGORY_PRIVATE_MESSAGE){
            Uri uri = Uri.parse("android.resource://yogispark.chat/drawable/ic_action_contact");
            holder.getContactImage().setImageURI(uri);
        }else{
            Uri uri = Uri.parse("android.resource://yogispark.chat/drawable/ic_action_group");
            holder.getContactImage().setImageURI(uri);
        }

        if(Data.get(position).Count == 0){
            holder.getCount().setVisibility(View.GONE);
        }else{
            holder.getCount().setVisibility(View.VISIBLE);
        }

        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.get(position).Count = 0;
                notifyItemChanged(position);

                Intent chat = new Intent(holder.getCardView().getContext(), ChatView.class);
                chat.putExtra("name", Data.get(position).Contact_Name);
                chat.putExtra("contact_id",Data.get(position).Contact_Id); //contact_id when private message, group_id as contact_id when group message
                chat.putExtra("category", Data.get(position).Category);
                holder.getCardView().getContext().startActivity(chat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    public void addMessage(MessageView messageView){
        Data.add(messageView);
        notifyItemChanged(Data.size()-1);
    }
}
