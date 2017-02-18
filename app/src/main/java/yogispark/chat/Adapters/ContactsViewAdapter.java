package yogispark.chat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import yogispark.chat.Models.Contact;
import yogispark.chat.R;
import yogispark.chat.UI.ChatView;
import yogispark.chat.Utility.Constants;
import yogispark.chat.Utility.Tools;
import yogispark.chat.ViewHolder.ContactsViewHolder;

/**
 * Created by yogesh on 14/10/16.
 */
public class ContactsViewAdapter extends RecyclerView.Adapter<ContactsViewHolder> {

    Context context;
    ArrayList<Contact> Contacts;
    SimpleDateFormat dateFormat;

    public ContactsViewAdapter(Context context, ArrayList<Contact> Contacts){
        this.context = context;
        this.Contacts = Contacts;
        dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm");
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_cardview,parent,false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, final int position) {
        holder.getContactName().setText(Contacts.get(position).Name);
        holder.getContactStatus().setText(Contacts.get(position).Status);

        Date date = Tools.getGMTDate(Contacts.get(position).Join_Date);
        String join_date = date != null ? dateFormat.format(date) : "";
        holder.getJoinDate().setText(join_date);

        Uri uri = Uri.parse("android.resource://yogispark.chat/drawable/ic_action_contact");
        holder.getProfileImage().setImageURI(uri);

        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat = new Intent(context, ChatView.class);
                chat.putExtra("name", Contacts.get(position).Name);
                chat.putExtra("contact_id", Contacts.get(position).Contact_Id);
                chat.putExtra("category",  Constants.CATEGORY_PRIVATE_MESSAGE);
                context.startActivity(chat);

                if(ChatView.CONTACT_ID != null){
                    ((AppCompatActivity)context).finish();
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return Contacts.size();
    }
}
