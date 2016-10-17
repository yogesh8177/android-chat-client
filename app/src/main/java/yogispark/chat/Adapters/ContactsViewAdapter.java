package yogispark.chat.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import yogispark.chat.Models.Contact;
import yogispark.chat.R;
import yogispark.chat.ViewHolder.ContactsViewHolder;

/**
 * Created by yogesh on 14/10/16.
 */
public class ContactsViewAdapter extends RecyclerView.Adapter<ContactsViewHolder> {

    Context context;
    ArrayList<Contact> Contacts;

    public ContactsViewAdapter(Context context, ArrayList<Contact> Contacts){
        this.context = context;
        this.Contacts = Contacts;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_cardview,parent,false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        holder.getContactName().setText(Contacts.get(position).Name);
        holder.getContactStatus().setText(Contacts.get(position).Status);
        holder.getJoinDate().setText(Contacts.get(position).Join_Date.substring(0,11));
    }

    @Override
    public int getItemCount() {
        return Contacts.size();
    }
}
