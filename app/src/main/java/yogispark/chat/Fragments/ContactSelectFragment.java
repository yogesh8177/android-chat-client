package yogispark.chat.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import yogispark.chat.Adapters.ContactsSelectViewAdapter;
import yogispark.chat.Asynctasks.FetchContacts;
import yogispark.chat.Models.Contact;
import yogispark.chat.R;
import yogispark.chat.UI.ContactSelect;
import yogispark.chat.UI.CreateGroup;
import yogispark.chat.Utility.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactSelectFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Contact>>{

    ArrayList<Contact> Contacts;
    RecyclerView recyclerView, selectedContacts;
    ContactsSelectViewAdapter adapter;
    boolean isLoading=false;

    public ContactSelectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Contacts = new ArrayList<>();
        adapter = new ContactsSelectViewAdapter(getContext(), Contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_contact_select, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContactSelect.CONTACT_SELECT_FOR == Constants.CONTACT_SELECT_FOR_NEW_GROUP)
                    startGroupCreateActivity(view);
                else if(ContactSelect.CONTACT_SELECT_FOR == Constants.CONTACT_SELECT_FOR_ADD_TO_GROUP)
                    addMembersToGroup(view, ContactSelect.GROUP_ID);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.contact_select_recyclerview);
        selectedContacts = (RecyclerView) view.findViewById(R.id.selected_contact_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        selectedContacts.setLayoutManager(new LinearLayoutManager(getContext()));

        prepareData();

        return view;
    }

    private void prepareData() {
        if(Contacts.size() == 0 && !isLoading){
            getActivity().getSupportLoaderManager().restartLoader(0,null,this).forceLoad();
        }else if(Contacts.size() == 0 && isLoading){
            getActivity().getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    @Override
    public Loader<ArrayList<Contact>> onCreateLoader(int id, Bundle args) {
        isLoading = true;
        return new FetchContacts(getContext(), ContactSelect.CONTACT_SELECT_FOR);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Contact>> loader, ArrayList<Contact> data) {
        isLoading = false;
        Contacts.clear();
        Contacts.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Contact>> loader) {

    }

    private void startGroupCreateActivity(View view){
        if(adapter.getSelectedContacts().size()!=0){
            Intent intent = new Intent(getContext(), CreateGroup.class);
            intent.putParcelableArrayListExtra("Contacts", adapter.getSelectedContacts());
            startActivity(intent);
        }else{
            Snackbar.make(view, "You must select atleast one contact!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    void addMembersToGroup(View view, String Group_Id){
        if(adapter.getSelectedContacts().size()!=0){
            Intent addMembers = new Intent();
            addMembers.setAction(Constants.GROUP_FILTER);
            addMembers.putExtra("category", Constants.GROUP_MEMBERS_ADDED);
            addMembers.putExtra("group_id", Group_Id);
            addMembers.putParcelableArrayListExtra("Contacts", adapter.getSelectedContacts());

            getActivity().sendBroadcast(addMembers);
            getActivity().finish();
        }else{
            Snackbar.make(view, "You must select atleast one contact!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
