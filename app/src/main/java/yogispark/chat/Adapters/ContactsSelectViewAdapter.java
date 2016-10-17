package yogispark.chat.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

import yogispark.chat.Models.Contact;
import yogispark.chat.Models.Parcelable.ParcelContacts;
import yogispark.chat.R;
import yogispark.chat.Utility.Constants;
import yogispark.chat.ViewHolder.ContactsViewHolder;

/**
 * Created by yogesh on 15/10/16.
 */
public class ContactsSelectViewAdapter extends RecyclerView.Adapter<ContactsViewHolder> {

    Context context;
    ArrayList<Contact> Contacts;
    ArrayList<ParcelContacts> SelectedContacts;


    public ContactsSelectViewAdapter(Context context, ArrayList<Contact> Contacts){
        this.context = context;
        this.Contacts = Contacts;
        SelectedContacts = new ArrayList<>();
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_cardview,parent,false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, final int position) {
        holder.getContactName().setText(Contacts.get(position).Name);
        holder.getContactStatus().setText(Contacts.get(position).Status);
        holder.getJoinDate().setText(Contacts.get(position).Join_Date);

        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Contacts.get(position).isSelected = !Contacts.get(position).isSelected;
                if(Contacts.get(position).isSelected)
                    holder.getCardView().setCardBackgroundColor(Color.GREEN);
                else
                    holder.getCardView().setCardBackgroundColor(Color.WHITE);

                addToSelectedList(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return Contacts.size();
    }

    void addToSelectedList(int position){
        boolean exists = false;

        ParcelContacts contact = new ParcelContacts(Contacts.get(position).Contact_Id, Contacts.get(position).Name, Contacts.get(position).Status, Contacts.get(position).Join_Date);
        if(SelectedContacts.size() == 0){
            SelectedContacts.add(contact);//Log.d("First Added:", contact.Contact_Name+", size: "+SelectedContacts.size());
        }else{
            for(int i=0; i<SelectedContacts.size(); i++){
                if(SelectedContacts.get(i).Contact_ID.equals(contact.Contact_ID)){
                    SelectedContacts.remove(i);
                    //Log.d("Removed:", contact.Contact_Name+", size: "+SelectedContacts.size());
                    exists = true;
                    break;
                }
            }

            if(!exists){
                SelectedContacts.add(contact);//Log.d("Added:", contact.Contact_Name+", size: "+SelectedContacts.size());
            }

        }


    }

    public ArrayList<ParcelContacts> getSelectedContacts() {
        return SelectedContacts;
    }
}
