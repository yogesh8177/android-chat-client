package yogispark.chat.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import yogispark.chat.Models.Contact;
import yogispark.chat.Models.Parcelable.ParcelContacts;
import yogispark.chat.R;
import yogispark.chat.ViewHolder.ContactsViewHolder;

/**
 * Created by yogesh on 15/10/16.
 */
public class SelectedContactsAdapter extends RecyclerView.Adapter<ContactsViewHolder>{

    Context context;
    ArrayList<ParcelContacts> Contacts;

    public SelectedContactsAdapter(Context context, ArrayList<ParcelContacts> Contacts){
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
        holder.getContactName().setText(Contacts.get(position).Contact_Name);
        holder.getContactStatus().setText(Contacts.get(position).Status);
        holder.getJoinDate().setText(Contacts.get(position).Join_Date);
    }

    @Override
    public int getItemCount() {
        return Contacts.size();
    }

}
