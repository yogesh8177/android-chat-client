package yogispark.chat.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import yogispark.chat.Adapters.SelectedContactsAdapter;
import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.Contact;
import yogispark.chat.Models.GroupMember;
import yogispark.chat.Models.Message;
import yogispark.chat.Models.Parcelable.ParcelContacts;
import yogispark.chat.Models.User;
import yogispark.chat.R;
import yogispark.chat.Utility.Constants;
import yogispark.chat.Utility.Tools;

public class CreateGroup extends AppCompatActivity {

    ArrayList<ParcelContacts> SelectedContacts;
    RecyclerView recyclerView;
    EditText group_name;
    SelectedContactsAdapter adapter;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        group_name = (EditText) findViewById(R.id.group_name);
        recyclerView = (RecyclerView) findViewById(R.id.selected_contact_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCreateGroupBroadcast(view);
            }
        });

        if(savedInstanceState == null){
            SelectedContacts = getIntent().getParcelableArrayListExtra("Contacts");
            adapter = new SelectedContactsAdapter(this,SelectedContacts);
            recyclerView.setAdapter(adapter);
        }else{
            SelectedContacts = savedInstanceState.getParcelableArrayList("Contacts");
            adapter = new SelectedContactsAdapter(this,SelectedContacts);
            recyclerView.setAdapter(adapter);
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleBroadcast(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.GROUP_CREATED_FILTER);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("Contacts", SelectedContacts);
        super.onSaveInstanceState(outState);
    }

    void sendCreateGroupBroadcast(View view){
        if(group_name.getText().length() > 2){
            Intent broadcast = new Intent();
            broadcast.setAction(Constants.GROUP_FILTER);
            broadcast.putExtra("category", Constants.GROUP_CREATE);
            broadcast.putExtra("GroupName", group_name.getText().toString());
            broadcast.putParcelableArrayListExtra("Contacts", SelectedContacts);
            sendBroadcast(broadcast);
        }else{
            Snackbar.make(view, "Enter a valid group name", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    void handleBroadcast(Intent intent){
        Contact contact = new Contact();
        contact.Contact_Id = intent.getStringExtra("GroupId");
        contact.Join_Date = intent.getStringExtra("CreatedAt");
        contact.Type = Constants.CONTACT_TYPE_GROUP;
        contact.Name = group_name.getText().toString();
        contact.Status = "Group status";

        new AddCreatedGroup(contact).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class AddCreatedGroup extends AsyncTask<Void, Void, Void>{

        Contact contact;
        SqlHelper helper;

        public AddCreatedGroup(Contact contact){
            this.contact = contact;
        }

        @Override
        protected Void doInBackground(Void... params) {
            helper = new SqlHelper(getApplicationContext());

            helper.insertContact(contact); //Add group to contacts
            helper.insertGroupMap(contact.Contact_Id, Tools.getGroupMemberFromSelectedContacts(CreateGroup.this,SelectedContacts));
            Message message = new Message();

            message.Contact_Id = contact.Contact_Id;
            message.Message_Id= new Date().toString();
            Log.d("MessageId", message.Message_Id);
            message.From = contact.Contact_Id;
            message.Body = "New group created";
            message.Type = "group-create";
            message.Category = Constants.CATEGORY_GROUP_MESSAGE;
            message.PostedTime = contact.Join_Date;

            helper.insertMessage(message,Constants.MESSAGE_RECEIVE); //insert welcome message for new group created



            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Toast.makeText(CreateGroup.this,"Group successfully created!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}
