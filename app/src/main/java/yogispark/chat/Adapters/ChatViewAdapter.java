package yogispark.chat.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import yogispark.chat.Dialogs.MessageDetailsDialog;
import yogispark.chat.Models.Message;
import yogispark.chat.R;
import yogispark.chat.UI.ChatView;
import yogispark.chat.Utility.Constants;
import yogispark.chat.Utility.Tools;
import yogispark.chat.ViewHolder.ChatViewHolder;

/**
 * Created by yogesh on 5/10/16.
 */
public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    Context context;
    ArrayList<Message> Messages;
    FragmentManager fragmentManager;
    SimpleDateFormat simpleDateFormat;
    String dateIndicator, oldDate=null;
    int TYPE_ME = 1;
    int TYPE_SENDER = 2;

    public ChatViewAdapter(Context context, ArrayList<Message> Messages){
        this.Messages = Messages;
        this.context = context;
        simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == TYPE_ME){
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_cardview_right,parent,false);
            return new ChatViewHolder(view);
        }else if(viewType == TYPE_SENDER){
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_cardview_left,parent,false);
            return new ChatViewHolder(view);
        }
        return  null;

    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder,final int position) {
        holder.getContactName().setText(Messages.get(position).Contact_Name);
        holder.getContactStatus().setText(Messages.get(position).Contact_Status);
        holder.getMessage().setText(Messages.get(position).Body);

        if(Messages.get(position).showDateIndicator){
            holder.getDateIndicator().setText(Messages.get(position).DateIndicator);
            holder.getDateIndicator().setVisibility(View.VISIBLE);
        }else{
            holder.getDateIndicator().setVisibility(View.GONE);
        }

        Date date = Tools.getGMTDate(Messages.get(position).PostedTime);
        String postedTime = date !=null ? simpleDateFormat.format(date) : "";
        holder.getPostedDate().setText(postedTime);

        holder.getContactStatus().setVisibility(View.GONE);// Change visibility

        if(holder.getItemViewType() == TYPE_ME){
            if(Messages.get(position).PostedTime!=null){
                Uri uri = Uri.parse("android.resource://yogispark.chat/drawable/ic_action_done");
                holder.getTicks().setImageURI(uri);
            }else{
                Uri uri = Uri.parse("android.resource://yogispark.chat/drawable/ic_action_alarm");
                holder.getTicks().setImageURI(uri);
            }
            if(Messages.get(position).DeliveryTime!=null){
                Uri uri = Uri.parse("android.resource://yogispark.chat/drawable/ic_action_done_all");
                holder.getTicks().setImageURI(uri);
            }else{
                holder.getPostedDate().setTextColor(Color.RED);
            }
        }


        holder.getCardView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(ChatView.CURRENT_USER.equals(Messages.get(position).From))
                    showDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return Messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(ChatView.CURRENT_USER.equals(Messages.get(position).From)){
            if(ChatView.Category == Constants.CATEGORY_GROUP_MESSAGE)
                Messages.get(position).Contact_Name = "Me";
            return TYPE_ME;
        }else{
            return TYPE_SENDER;
        }
    }


    void showDialog(int position){

            MessageDetailsDialog dialog = new MessageDetailsDialog();
            dialog.GroupId = ChatView.CONTACT_ID;
            dialog.MessageId = Messages.get(position).Message_Id;
            dialog.Category = Messages.get(position).Category;
            dialog.DeliveredDate = Messages.get(position).DeliveryTime != null ?  Messages.get(position).DeliveryTime.toString() : "";
            dialog.ReadDate = Messages.get(position).ReadTime != null ? Messages.get(position).ReadTime.toString() : "";

            fragmentManager = ((ChatView)context).getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            Fragment prev = fragmentManager.findFragmentByTag("dialog");
            if(prev!=null){
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            dialog.show(ft,"dialog");


    }


}
