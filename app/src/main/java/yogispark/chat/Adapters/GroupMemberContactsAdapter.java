package yogispark.chat.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import yogispark.chat.Dialogs.GroupMemberInfo;
import yogispark.chat.Models.Contact;
import yogispark.chat.R;
import yogispark.chat.UI.GroupInfo;
import yogispark.chat.Utility.Constants;
import yogispark.chat.ViewHolder.ChatViewHolder;
import yogispark.chat.ViewHolder.ContactsViewHolder;

/**
 * Created by yogesh on 17/10/16.
 */
public class GroupMemberContactsAdapter extends RecyclerView.Adapter<ContactsViewHolder> {
    Context context;
    ArrayList<Contact> Contacts;
    FragmentManager fragmentManager;
    String Group_Id;

    public GroupMemberContactsAdapter(Context context, ArrayList<Contact> Contacts, FragmentManager fragmentManager, String groupId){
        this.context = context;
        this.Contacts = Contacts;
        this.fragmentManager = fragmentManager;
        this.Group_Id = groupId;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_cardview,parent,false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder,final int position) {
        holder.getContactName().setText(Contacts.get(position).Name);
        holder.getContactStatus().setText(Contacts.get(position).Status);
        holder.getJoinDate().setText(Contacts.get(position).Join_Date !=null ? Contacts.get(position).Join_Date : "");

        Uri uri = Uri.parse("android.resource://yogispark.chat/drawable/ic_action_contact");
        holder.getProfileImage().setImageURI(uri);

        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return Contacts.size();
    }


    private void showDialog(int position) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if(prev!=null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        GroupMemberInfo dialog = new GroupMemberInfo();
        dialog.GroupId = Group_Id;
        dialog.MemberId = Contacts.get(position).Contact_Id;

        dialog.show(ft,"dialog");
    }
}
