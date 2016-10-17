package yogispark.chat.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import yogispark.chat.Adapters.ContactsViewAdapter;
import yogispark.chat.Asynctasks.FetchContacts;
import yogispark.chat.Models.Contact;
import yogispark.chat.R;
import yogispark.chat.Utility.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Contact>>{

    ArrayList<Contact> Contacts;
    RecyclerView recyclerView;
    ContactsViewAdapter adapter;
    boolean isLoading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Contacts = new ArrayList<>();
        adapter = new ContactsViewAdapter(getContext(),Contacts);
    }

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.contacts_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        prepareData();

        return view;
    }

    private void prepareData() {
        if(Contacts.size() == 0 && !isLoading){
            //fetch data
            getActivity().getSupportLoaderManager().restartLoader(0,null,this).forceLoad();
        }else if(Contacts.size() == 0 && isLoading){
            getActivity().getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    @Override
    public Loader<ArrayList<Contact>> onCreateLoader(int id, Bundle args) {
        isLoading = true;
        return new FetchContacts(getContext(), Constants.CONTACT_SELECT_FOR_NEW_GROUP); //FOR NEW GROUP returns all private contacts
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Contact>> loader, ArrayList<Contact> data) {
        isLoading = false;
        Contacts.clear();
        Contacts.addAll(data);
        Log.d("Contacts list",""+data.size());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Contact>> loader) {

    }
}
