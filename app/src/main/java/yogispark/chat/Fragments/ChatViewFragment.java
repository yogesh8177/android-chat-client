package yogispark.chat.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import yogispark.chat.Adapters.ChatViewAdapter;
import yogispark.chat.Asynctasks.ResetMessageCount;
import yogispark.chat.Asynctasks.UpdateMessageView;
import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.Message;
import yogispark.chat.R;
import yogispark.chat.Service.ChatService;
import yogispark.chat.UI.ChatView;
import yogispark.chat.Utility.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatViewFragment extends Fragment {


    RecyclerView recyclerView;
    ChatViewAdapter adapter;
    ArrayList<Message> Messages;
    BroadcastReceiver receiver;
    IntentFilter filter;
    EditText input;
    Button sendMessage;

    public ChatViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Messages = new ArrayList<>();
        adapter = new ChatViewAdapter(Messages);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Constants.NEW_MESSAGE_FILTER)){
                    if(intent.getStringExtra("contact_id").equals(ChatView.CONTACT_ID) || intent.getStringExtra("contact_id").equals(ChatView.CURRENT_USER)) {   //If message is for current contact, then proceed
                        updateMessage(intent);
                    }
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction(Constants.NEW_MESSAGE_FILTER);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_chat_view, container, false);
        input = (EditText) view.findViewById(R.id.message_input);
        sendMessage = (Button) view.findViewById(R.id.send_message);
        recyclerView = (RecyclerView) view.findViewById(R.id.chat_view_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(input.getText().toString());
            }
        });
        getActivity().registerReceiver(receiver,filter);
        prepareDate();
        return view;
    }

    private void prepareDate() {
        if(Messages.size()==0){
            //populate Messages and set adapter to recyclerview
            new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            Toast.makeText(getContext(),"Has data",Toast.LENGTH_SHORT).show();
        }
    }

    void updateMessage(Intent intent){

        switch (intent.getIntExtra("message_type",0)){
            case Constants.NEW_MESSAGE:
                addNewMessage(intent);
                break;

            case Constants.MESSAGE_POSTED:
                updatePostedMessage(intent);
                break;

            case  Constants.MESSAGE_DELIVERED:
                updateDeliveredMessage(intent);
                break;

            case Constants.MESSAGE_READ:
                updateReadMessage(intent);
                break;
        }

    }

    void addNewMessage(Intent intent){
        Message message = new Message();
        message.Contact_Id = intent.getStringExtra("contact_id");
        message.Message_Id = intent.getStringExtra("message_id");
        message.Contact_Name = intent.getStringExtra("from");
        message.Category = intent.getIntExtra("category",0);
        message.PostedTime = intent.getStringExtra("posted_time");
        message.From = intent.getStringExtra("from");
        message.Body = intent.getStringExtra("body");

        Messages.add(message);
        adapter.notifyItemInserted(Messages.size()-1);
        recyclerView.scrollToPosition(Messages.size()-1);
        //Reset latest messages count for this contact to 0
        new ResetMessageCount(getContext(),ChatView.CONTACT_ID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    void updatePostedMessage(Intent intent){
        Message message = new Message();
        message.Local_Id = intent.getLongExtra("local_id",0);
        message.Contact_Id = intent.getStringExtra("contact_id"); //Not needed.....
        message.Message_Id = intent.getStringExtra("message_id");
        message.PostedTime = intent.getStringExtra("posted_time");


        for(int i= Messages.size()-1; i >=0; i--){

            if(Messages.get(i).Local_Id == message.Local_Id){

                Messages.get(i).Message_Id = message.Message_Id;
                Messages.get(i).PostedTime = message.PostedTime;
                adapter.notifyItemChanged(i);
                Log.d("Matched","Id: "+message.Local_Id+", MessageId: "+message.Message_Id);
                break;
            }
        }

    }

    void updateDeliveredMessage(Intent intent){
        Message message = new Message();
        message.Message_Id = intent.getStringExtra("message_id");
        message.DeliveryTime = intent.getStringExtra("delivery_time");


        for(int i= Messages.size()-1; i >=0; i--){

            if(Messages.get(i).Message_Id.equals(message.Message_Id)){
                Messages.get(i).DeliveryTime = message.DeliveryTime;
                adapter.notifyItemChanged(i);
                break;
            }
        }

    }

    void updateReadMessage(Intent intent){
        Message message = new Message();
        message.Message_Id = intent.getStringExtra("message_id");
        message.ReadTime = intent.getStringExtra("read_time");


        for(int i= Messages.size()-1; i >=0; i--){

            if(Messages.get(i).Message_Id.equals(message.Message_Id)){
                Messages.get(i).ReadTime = message.ReadTime;
                adapter.notifyItemChanged(i);
                break;
            }
        }

    }

    void sendMessage(final String message){
        if(message.length() != 0 ){
            new AsyncTask<Void,Void,Message>(){

                @Override
                protected Message doInBackground(Void... params) {
                    Message msg = new Message();
                    msg.Body = message;
                    msg.Category = ChatView.Category;
                    msg.From = ChatView.CURRENT_USER;
                    msg.Contact_Id = ChatView.CONTACT_ID;
                    msg.Require_Push = 1;

                    SqlHelper helper = new SqlHelper(getContext());
                    long row = helper.insertMessage(msg, Constants.MESSAGE_SEND);
                    msg.Local_Id = row;

                    return msg;
                }

                @Override
                protected void onPostExecute(Message message){
                    Messages.add(message);
                    adapter.notifyItemInserted(Messages.size() - 1);
                    recyclerView.scrollToPosition(Messages.size() - 1);
                    sendMessageBroadcast(message); //Send to background service to upload to server
                    Toast.makeText(getContext(),"Row inserted: "+message.Local_Id,Toast.LENGTH_SHORT).show();
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }

    void sendMessageBroadcast(Message message){
        Intent intent = new Intent();
        intent.setAction(Constants.SEND_MESSAGE_FILTER);

        intent.putExtra("contact_id", message.Contact_Id);
        intent.putExtra("local_id", message.Local_Id);
        intent.putExtra("from", message.From);
        intent.putExtra("body", message.Body);
        intent.putExtra("category", ChatView.Category);

        getContext().sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    class BackgroundTask extends AsyncTask<Void,Void,ArrayList<Message>>{

        @Override
        protected ArrayList<Message> doInBackground(Void... params) {
            SqlHelper helper = new SqlHelper(getContext());

            return helper.getMessages(ChatView.CONTACT_ID,ChatView.Category);
        }

        @Override
        protected void onPostExecute(ArrayList<Message> Result){
            Messages.addAll(Result);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(Messages.size() - 1);

            Log.d("Message: ",""+adapter.getItemCount());

        }
    }
}
