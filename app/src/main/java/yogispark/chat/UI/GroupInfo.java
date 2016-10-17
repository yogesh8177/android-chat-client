package yogispark.chat.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import yogispark.chat.Adapters.ContactsSelectViewAdapter;
import yogispark.chat.Adapters.ContactsViewAdapter;
import yogispark.chat.Adapters.GroupMemberContactsAdapter;
import yogispark.chat.Asynctasks.FetchContacts;
import yogispark.chat.Asynctasks.FetchGroupMembers;
import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.Contact;
import yogispark.chat.Models.Parcelable.ParcelContacts;
import yogispark.chat.R;
import yogispark.chat.Utility.Constants;

public class GroupInfo extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Contact>>{

    ArrayList<Contact> Contacts;
    RecyclerView recyclerView;
    TextView groupName;
    Button AddToGroup, ExitGroup;
    GroupMemberContactsAdapter adapter;
    boolean isLoading = false;
    String Group_Id, Name;
    FragmentManager fragmentManager;
    BroadcastReceiver receiver;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Group_Id = savedInstanceState != null ? savedInstanceState.getString("GroupId") : getIntent().getStringExtra("GroupId");
        Name = savedInstanceState !=null ? savedInstanceState.getString("Name") : getIntent().getStringExtra("Name");


        fragmentManager = getSupportFragmentManager();
        Contacts = new ArrayList<>();
        adapter = new GroupMemberContactsAdapter(this, Contacts, fragmentManager, Group_Id);

        recyclerView = (RecyclerView) findViewById(R.id.group_members_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        groupName = (TextView) findViewById(R.id.group_name);
        AddToGroup = (Button) findViewById(R.id.add_to_group);
        AddToGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContactSelectActivity(GroupInfo.this);
                finish();
            }
        });
        ExitGroup = (Button) findViewById(R.id.exit_group);
        ExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitGroup();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleBroadcast(intent);
            }
        };
        filter = new IntentFilter();
        filter.addAction(Constants.GROUP_EXIT_FILTER);
        registerReceiver(receiver,filter);

        prepareData(savedInstanceState);
    }

    private void prepareData(Bundle saved) {

        isLoading = saved != null ? saved.getBoolean("isLoading") : false;

        groupName.setText(Name);

        if(Contacts.size() == 0 && !isLoading){
            getSupportLoaderManager().restartLoader(0, null,this).forceLoad();
        }else if(Contacts.size() == 0 && isLoading){
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("Name",Name);
        outState.putBoolean("isLoading", isLoading);
        outState.putString("GroupId",Group_Id);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<ArrayList<Contact>> onCreateLoader(int id, Bundle args) {
        isLoading = true;
        return new FetchGroupMembers(GroupInfo.this, Group_Id);
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

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    void startContactSelectActivity(Context context){
        Intent select = new Intent(context, ContactSelect.class);
        select.putExtra("CONTACT_SELECT_FOR", Constants.CONTACT_SELECT_FOR_ADD_TO_GROUP); //CONTACT_SELECT_FOR_ADD_TO_GROUP selects only those contacts that are not in current group
        select.putExtra("GroupId", Group_Id);
        startActivity(select);
    }

    void exitGroup(){
        AlertDialog dialog = new AlertDialog.Builder(GroupInfo.this)
                .setTitle("Exit group: "+Name)
                .setMessage("Are you sure?")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent exit = new Intent();
                        exit.setAction(Constants.GROUP_FILTER);

                        exit.putExtra("category", Constants.GROUP_EXIT);
                        exit.putExtra("group_id", Group_Id);

                        sendBroadcast(exit);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();;
                    }
                }).create();
        dialog.show();

    }

    void handleBroadcast(Intent intent){
        if(intent.getStringExtra("group_id").equals(Group_Id)){
            int status = intent.getIntExtra("status",0);
            if(status == Constants.GROUP_EXIT_SUCCESSFULL){
                Toast.makeText(getApplicationContext(),"Group exited!",Toast.LENGTH_SHORT).show();
                finish();
            }else if(status == Constants.GROUP_EXIT_ERROR){
                Toast.makeText(getApplicationContext(),"Could not exit, try again!",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
