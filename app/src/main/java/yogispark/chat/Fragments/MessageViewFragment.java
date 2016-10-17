package yogispark.chat.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import yogispark.chat.Adapters.MessageViewRecyclerAdapter;
import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.GroupMember;
import yogispark.chat.Models.MessageView;
import yogispark.chat.R;
import yogispark.chat.Utility.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageViewFragment extends Fragment {

    RecyclerView recyclerView;
    MessageViewRecyclerAdapter adapter;
    ArrayList<MessageView> Messages;
    BroadcastReceiver receiver;

    public static MessageViewFragment newInstance(){
        MessageViewFragment fragment = new MessageViewFragment();

        return fragment;
    }
    public MessageViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Messages = new ArrayList<>();
        adapter = new MessageViewRecyclerAdapter(getContext(),Messages);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Constants.NEW_MESSAGE_FILTER)){
                    if(intent.getIntExtra("message_type",0) == Constants.NEW_MESSAGE)
                        updateMessageView(intent);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.NEW_MESSAGE_FILTER);
        getActivity().registerReceiver(receiver,filter);

    }

    private void updateMessageView(Intent intent) {
        int type = intent.getIntExtra("category",0);
        switch (type){
            case Constants.CATEGORY_PRIVATE_MESSAGE:
                addPrivateMessage(intent);
            break;

            case Constants.CATEGORY_GROUP_MESSAGE:
                addGroupMessage(intent);
            break;

            default:
                Toast.makeText(getContext(),"Invalid message received",Toast.LENGTH_SHORT).show();
            break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_message_view, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.message_view_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        prepareData();
        return view;
    }

    private void prepareData() {
        if(Messages.size()!=0){
            recyclerView.setAdapter(adapter);
        }else{
            //populate data from db
            new LoadMessageView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }



    private void addPrivateMessage(Intent intent) {
        MessageView message = new MessageView();
        message.Contact_Id = intent.getStringExtra("contact_id");
        message.Contact_Name = intent.getStringExtra("name");
        message.Message_Id = intent.getStringExtra("message_id");
        message.Body = intent.getStringExtra("body");
        message.Posted_Date = intent.getStringExtra("posted_time").substring(0,11);
        message.Category = intent.getIntExtra("category",0);

        int index=-1;
        for(int i=0; i<Messages.size(); i++){
            if(Messages.get(i).Contact_Id.equals(message.Contact_Id)){
                message.Count = Messages.get(i).Count+1;
                message.Contact_Name = Messages.get(i).Contact_Name;
                Messages.remove(i);
                Messages.add(i,message);
                index=i;
                adapter.notifyItemChanged(index);
                break;
            }
        }
        if(index == -1){ //if new item, then add to list and set count to 1 (Happens for the first time when new contact sends msg)
            message.Count = 1;
            Messages.add(message);
            adapter.notifyItemInserted(Messages.size() - 1);
        }

    }

    private void addGroupMessage(Intent intent) {
        MessageView message = new MessageView();
        message.Contact_Id = intent.getStringExtra("contact_id");
        message.Contact_Name = intent.getStringExtra("name");
        message.From = intent.getStringExtra("From");
        message.Message_Id = intent.getStringExtra("message_id");
        message.Body = intent.getStringExtra("body");
        message.Posted_Date = intent.getStringExtra("posted_time").substring(0,11);
        message.Category = intent.getIntExtra("category",0);

        int index=-1;
        for(int i=0; i<Messages.size(); i++){
            if(Messages.get(i).Contact_Id.equals(message.Contact_Id)){
                message.Count = Messages.get(i).Count+1;
                message.Contact_Name = Messages.get(i).Contact_Name;
                Messages.remove(i);
                Messages.add(i,message);
                index=i;
                adapter.notifyItemChanged(index);
                break;
            }
        }
        if(index == -1){
            message.Count = 1;
            Messages.add(message);
            adapter.notifyItemInserted(Messages.size() - 1);
        }
    }

    class LoadMessageView extends AsyncTask<Void,Void,ArrayList<MessageView>>{

        @Override
        protected ArrayList<MessageView> doInBackground(Void... params) {
            SqlHelper helper = new SqlHelper(getContext());

            return helper.getMessageView();
        }

        @Override
        protected void onPostExecute(ArrayList<MessageView> Result){
            Messages.addAll(Result);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            Toast.makeText(getContext(),""+Result.size(),Toast.LENGTH_SHORT).show();
        }
    }
}
