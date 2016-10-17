package yogispark.chat.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.Contact;
import yogispark.chat.Models.GroupMember;
import yogispark.chat.Models.GroupMessage;
import yogispark.chat.Models.Message;
import yogispark.chat.Models.Parcelable.ParcelContacts;
import yogispark.chat.Models.User;
import yogispark.chat.Utility.Constants;
import yogispark.chat.Utility.Tools;

public class ChatService extends Service {

    private Socket socket;
    private BroadcastReceiver receiver, group_receiver;
    SqlHelper db;
    User user;
    private Emitter.Listener onConnected, onDisconnected, onPrivateTyping, onGroupTyping, onPrivateMessage, onGroupMessage, onPrivatePosted, onGroupPosted, onPrivateDeliveryReceipt, onGroupDeliveryReceipt, onGroupCreated, onGroupMembersAdded, onGroupMembersRemoved, onAddedToNewGroup;

    public ChatService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            db = new SqlHelper(getApplicationContext());
            initializeUser();
            setupListeners();
            setupSocket();
            setUpReceivers();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        releaseSocket();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void setUpReceivers(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleBroadcast(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SEND_MESSAGE_FILTER);
        registerReceiver(receiver, filter);

        group_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleGroupBroadcast(intent);
            }
        };

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Constants.GROUP_FILTER);
        registerReceiver(group_receiver,filter1);

    }


    void setupSocket() throws URISyntaxException{
        socket = IO.socket(Constants.SOCKET_URL);
        socket.connect();
        socket.on("connected", onConnected);
        socket.on("disconnected", onDisconnected);
        socket.on("private-message", onPrivateMessage);
        socket.on("private-posted", onPrivatePosted);
        socket.on("private-delivery-receipt", onPrivateDeliveryReceipt);
        socket.on("group-message", onGroupMessage);
        socket.on("group-posted", onGroupPosted);
        socket.on("group-delivery-receipt", onGroupDeliveryReceipt);
        socket.on("group-created", onGroupCreated);
        socket.on("added-to-new-group", onAddedToNewGroup);
        socket.on("added-to-group", onGroupMembersAdded);
        socket.on("group-members-removed", onGroupMembersRemoved);
        socket.on("private-typing", onPrivateTyping);
        socket.on("group-typing", onGroupTyping);

    }

    void setupListeners(){
        onConnected = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject credentials = new JSONObject();
                try {
                    credentials.put("_id",user.ID);
                    credentials.put("name",user.Name);
                    credentials.put("mobile",user.Mobile);
                    credentials.put("token",user.Token);

                    socket.emit("connection-ack", credentials);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        onDisconnected = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
            }
        };

        onPrivateMessage = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try{
                    Message message = new Message();
                    message.Contact_Id = data.getString("from");
                    message.From = data.getString("from");
                    message.Message_Id = data.getString("_id");
                    message.Type = data.getString("type");
                    message.Body = data.getString("body");
                    message.PostedTime = data.getString("datetime");
                    message.Category = Constants.CATEGORY_PRIVATE_MESSAGE;
                    message.Require_Push = 0;
                    Log.d("Private-message: ", message.Contact_Id+", From: "+message.From);
                    db.insertMessage(message, Constants.MESSAGE_RECEIVE); //add to message table and message_view table

                    JSONObject ack = new JSONObject();
                    ack.put("_id",message.Message_Id);
                    socket.emit("private-delivered-ack",ack);

                    Tools.smallNotification(getApplicationContext(),message.Body,"Private Message");
                    sendPrivateBroadCast(message, Constants.NEW_MESSAGE);
                }catch (Exception e){
                    Log.d("Private-Message",e.getMessage());
                }

            }
        };

        onPrivatePosted = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];

                try{
                    Message message = new Message();
                    message.Contact_Id = user.ID; //used for receiving broadcast in chat view activity
                    message.Message_Id = data.getString("_id");
                    message.Local_Id = data.getLong("local_id");
                    message.PostedTime = data.getString("datetime");

                    db.updateMessage(message, SqlHelper.MESSAGE_POSTED);
                    db.updateMessageView(db.getContactId(message.Message_Id),message.Message_Id,SqlHelper.MESSAGE_VIEW_NO_INCREMENT);

                    Log.d("OnPrivatePosted",message.Message_Id);
                    JSONObject ack = new JSONObject();
                    ack.put("_id", message.Message_Id);
                    socket.emit("private-id-received", ack);

                    sendPrivateBroadCast(message, Constants.MESSAGE_POSTED);
                }catch (Exception e){
                    Log.d("OnPrivatePosted",e.getMessage());
                }

            }
        };

        onPrivateDeliveryReceipt = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try{
                    Message message = new Message();
                    message.Contact_Id = user.ID; //used for receiving broadcast in chat view activity
                    message.Message_Id = data.getString("_id");
                    message.DeliveryTime = data.getString("datetime");

                    db.updateMessage(message, SqlHelper.MESSAGE_DELIVERED);
Log.d("Delivered",message.Message_Id);
                    sendPrivateBroadCast(message, Constants.MESSAGE_DELIVERED);
                }catch (JSONException e){}
            }
        };

        onGroupMessage = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try{
                    Message message = new Message();
                    message.Message_Id = data.getString("_id");
                    message.Contact_Id = data.getString("group_id");
                    message.Group_Id = data.getString("group_id");
                    message.Body = data.getString("body");
                    message.Type = data.getString("type");
                    message.From = data.getString("from");
                    message.PostedTime = data.getString("datetime");
                    message.Category = Constants.CATEGORY_GROUP_MESSAGE;
                    message.Require_Push = 0;
Log.d("Group-message: ", message.Group_Id+", From: "+message.From);
                    db.insertMessage(message, Constants.MESSAGE_RECEIVE);

                    JSONObject ack = new JSONObject();
                    ack.put("_id",message.Message_Id);
                    ack.put("user_id", user.ID);

                    socket.emit("group-message-delivery-ack",ack);

                    Tools.smallNotification(getApplicationContext(),message.Body,"Group Message");
                    sendGroupBroadCast(message, Constants.NEW_MESSAGE);
                }catch (JSONException e){
                    Log.d("Group-Message",e.getMessage());
                }
            }
        };

        onGroupPosted = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];

                try{
                    GroupMessage message = new GroupMessage();
                    message.Contact_Id = user.ID; //used for receiving broadcast in chat view activity
                    message.Message_Id = data.getString("_id");
                    message.Local_Id = data.getLong("local_id");
                    message.PostedTime = data.getString("datetime");

                    db.updateMessage(message, SqlHelper.MESSAGE_POSTED);
                    db.updateMessageView(db.getContactId(message.Message_Id),message.Message_Id,SqlHelper.MESSAGE_VIEW_NO_INCREMENT);

                    JSONObject arg = new JSONObject();
                    arg.put("_id", message.Message_Id);
                    socket.emit("group-id-received", arg);

                    sendGroupBroadCast(message, Constants.MESSAGE_POSTED);
                }catch (JSONException e){
                    Log.d("Group-Posted",e.getMessage());
                }
            }
        };

        onGroupDeliveryReceipt = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];

                try{
                    Message message = new GroupMessage();
                    message.Contact_Id = user.ID; //used for receiving broadcast in chat view activity
                    message.Message_Id = data.getString("_id");
                    message.From = data.getString("delivered_to");
                    message.DeliveryTime = data.getString("datetime");
Log.d("Group-Delivered",message.From);
                    db.addGroupMessageMeta(message);

                    sendGroupBroadCast(message, Constants.MESSAGE_DELIVERED);
                }catch (JSONException e){}
            }
        };

        onGroupCreated = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];

                try {
                    String group_id = data.getString("_id");
                    String created_at = data.getString("datetime");

                    JSONObject arg = new JSONObject();
                    arg.put("group_id", group_id);
                    arg.put("user_id", user.ID);

                    Intent broadcast = new Intent();
                    broadcast.setAction(Constants.GROUP_CREATED_FILTER);
                    broadcast.putExtra("GroupId", group_id);
                    broadcast.putExtra("CreatedAt", created_at);
                    sendBroadcast(broadcast);

                    socket.emit("group-create-id-received-ack",arg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        onAddedToNewGroup = new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                Log.d("GroupMembersAddedToNew","Added");
                try{
                    ArrayList<GroupMember> Members = new ArrayList<>();

                    String group_id = data.getString("group_id");;

                    JSONArray jsonArray = data.getJSONArray("members");
                    for(int i=0; i<jsonArray.length();i++){
                        GroupMember gm = new GroupMember();
                        JSONObject object = (JSONObject) jsonArray.get(i);

                        gm.Group_Id = group_id;;
                        gm.Join_date = object.getString("join_date");
                        gm.Contact_Id = object.getString("user_id");

                        Members.add(gm);
                    }
                    db.insertGroupMap(group_id,Members);
                    addNewGroupContact(data);

                    Tools.smallNotification(getApplicationContext(),group_id,"Added to new group");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };

        onGroupMembersAdded = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
Log.d("GroupMembersAdded","Added");
                try{
                    ArrayList<GroupMember> members = new ArrayList<>();
                    String group_id = data.getString("_id");

                    JSONArray jsonArray = data.getJSONArray("members");
                    for(int i=0; i<jsonArray.length();i++){
                        GroupMember gm = new GroupMember();
                        JSONObject object = (JSONObject) jsonArray.get(i);

                        gm.Group_Id = group_id;
                        gm.Join_date = object.getString("join_date");
                        gm.Contact_Id = object.getString("user_id");

                        members.add(gm);
                    }
                db.insertGroupMap(group_id,members);
                sendGroupMembersBroadcast(group_id,members, Constants.GROUP_MEMBERS_ADDED);
                }catch (JSONException e){}
            }
        };

        onGroupMembersRemoved = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];

                try{
                    ArrayList<GroupMember> members = new ArrayList<>();
                    String group_id = data.getString("_id");
                    String remove_date =  data.getString("datetime");
                    String removed_by = data.getString("user_id");
        Log.d("Removed by",removed_by);
                    JSONArray jsonArray = data.getJSONArray("members");

                    for(int i=0; i<jsonArray.length();i++){

                        GroupMember gm = new GroupMember();
                        gm.Group_Id = group_id;
                        gm.Remove_Date = remove_date;
                        gm.Contact_Id = (String) jsonArray.get(i); //contains contact_id/user_id of removed members
Log.d("Members-Removed",gm.Contact_Id);
                        members.add(gm);
                    }
                    db.deleteGroupMap(group_id, members);
                    sendGroupMembersBroadcast(group_id,members, Constants.GROUP_MEMBERS_REMOVED);
                }catch (JSONException e){}
            }
        };

        onPrivateTyping = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
            }
        };

        onGroupTyping = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
            }
        };

    }

    void releaseSocket(){
        socket.disconnect();
        socket.off("connected", onConnected);
        socket.off("disconnected", onDisconnected);
        socket.off("private-message", onPrivateMessage);
        socket.off("private-posted", onPrivatePosted);
        socket.off("private-delivery-receipt", onPrivateDeliveryReceipt);
        socket.off("group-message", onGroupMessage);
        socket.off("group-posted", onGroupPosted);
        socket.off("group-delivery-receipt", onGroupDeliveryReceipt);
        socket.off("group-created", onGroupCreated);
        socket.off("added-to-new-group", onAddedToNewGroup);
        socket.off("added-to-group", onGroupMembersAdded);
        socket.off("group-members-removed", onGroupMembersRemoved);
        socket.off("private-typing", onPrivateTyping);
        socket.off("group-typing", onGroupTyping);
    }

    void sendPrivateBroadCast(Message message, int type){

        Intent intent = new Intent();
        intent.setAction(Constants.NEW_MESSAGE_FILTER);
        intent.putExtra("category",Constants.CATEGORY_PRIVATE_MESSAGE);

        switch (type){
            case Constants.NEW_MESSAGE:                     //New message arrives
                intent.putExtra("message_type",Constants.NEW_MESSAGE);

                intent.putExtra("message_id", message.Message_Id);
                intent.putExtra("contact_id", message.From);
                intent.putExtra("name", db.getContactName(message.From));
                intent.putExtra("from", message.From);
                intent.putExtra("body",message.Body);
                intent.putExtra("type", message.Type);
                intent.putExtra("posted_time", message.PostedTime);

            break;

            case Constants.MESSAGE_POSTED:                  // Message sent to server
                intent.putExtra("message_type",Constants.MESSAGE_POSTED);

                intent.putExtra("local_id", message.Local_Id);
                intent.putExtra("contact_id", message.Contact_Id);
                intent.putExtra("message_id", message.Message_Id);
                intent.putExtra("posted_time", message.PostedTime);

            break;

            case Constants.MESSAGE_DELIVERED:               //Message delivered to recipient
                intent.putExtra("message_type",Constants.MESSAGE_DELIVERED);

                intent.putExtra("contact_id", message.Contact_Id);
                intent.putExtra("message_id", message.Message_Id);
                intent.putExtra("delivery_time", message.DeliveryTime);
            break;

            case Constants.MESSAGE_READ:                    //Message read by recipient
                intent.putExtra("message_type",Constants.MESSAGE_READ);

                intent.putExtra("message_id", message.Message_Id);
                intent.putExtra("read_time", message.ReadTime);
            break;
        }

        sendBroadcast(intent);

    }

    void sendGroupBroadCast(Message message, int type){

        Intent intent = new Intent();
        intent.setAction(Constants.NEW_MESSAGE_FILTER);
        intent.putExtra("category",Constants.CATEGORY_GROUP_MESSAGE);

        switch (type){
            case Constants.NEW_MESSAGE:
                intent.putExtra("message_type",Constants.NEW_MESSAGE);

                intent.putExtra("local_id", message.Local_Id);
                intent.putExtra("message_id", message.Message_Id);
                intent.putExtra("contact_id", message.Group_Id);
                intent.putExtra("name", db.getContactName(message.Contact_Id));
                intent.putExtra("from", db.getContactName(message.From));
                intent.putExtra("body",message.Body);
                intent.putExtra("type", message.Type);
                intent.putExtra("posted_time", message.PostedTime);
                intent.putExtra("from", message.From);

            break;

            case Constants.MESSAGE_POSTED:
                intent.putExtra("message_type",Constants.MESSAGE_POSTED);
      Log.d("Posted-Group","Id: "+message.Contact_Id);
                intent.putExtra("local_id", message.Local_Id);
                intent.putExtra("contact_id", message.Contact_Id);
                intent.putExtra("message_id", message.Message_Id);
                intent.putExtra("posted_time", message.PostedTime);
            break;

            case Constants.MESSAGE_DELIVERED:
                intent.putExtra("message_type",Constants.MESSAGE_DELIVERED);

                intent.putExtra("contact_id", message.Contact_Id);
                intent.putExtra("message_id", message.Message_Id);
                intent.putExtra("group_id", message.Group_Id);
                intent.putExtra("delivery_time", message.DeliveryTime);
            break;

            case Constants.MESSAGE_READ:
                intent.putExtra("message_type",Constants.MESSAGE_READ);

                intent.putExtra("message_id", message.Message_Id);
                intent.putExtra("group_id", message.Group_Id);
                intent.putExtra("read_time", message.ReadTime);
            break;
        }

        sendBroadcast(intent);

    }

    void sendGroupMembersBroadcast(String group_id, ArrayList<GroupMember> Members, int type){
        Intent intent = new Intent();
        intent.setAction(Constants.NEW_MESSAGE_FILTER);
        intent.putExtra("category",Constants.CATEGORY_GROUP_MESSAGE);
        intent.putExtra("contact_id", group_id);
        sendBroadcast(intent);
    }
//Called when user sends message to recipient
    void handleBroadcast(Intent intent){

        switch (intent.getIntExtra("category",0)){
            case Constants.CATEGORY_PRIVATE_MESSAGE:
                sendPrivateMessage(intent);
                break;

            case Constants.CATEGORY_GROUP_MESSAGE:
                sendGroupMessage(intent);
                break;
        }
    }
//Handle when user tries to modify group
    void handleGroupBroadcast(Intent intent){
        switch (intent.getIntExtra("category",0)){

            case Constants.GROUP_CREATE:
                createGroup(intent);
            break;

            case Constants.GROUP_MEMBERS_ADDED:
                AddMembersToGroup(intent);
                break;

            case Constants.GROUP_MEMBERS_REMOVED:
                RemoveMembersFromGroup(intent);
                break;

            case Constants.GROUP_EXIT:
                exitGroup(intent);
                break;

        }
    }


    void sendPrivateMessage(Intent intent){


        try {
            JSONObject args = new JSONObject();
            args.put("local_id", intent.getLongExtra("local_id",0));
            args.put("from", intent.getStringExtra("from"));
            args.put("body", intent.getStringExtra("body"));
            args.put("to", intent.getStringExtra("contact_id"));
            args.put("type","normal");

            socket.emit("private-message",args);
            Log.d("Send-Private","Id: "+args.getLong("local_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void sendGroupMessage(Intent intent){

        try {
            JSONObject args = new JSONObject();
            args.put("local_id", intent.getLongExtra("local_id",0));
            args.put("from", intent.getStringExtra("from"));
            args.put("body", intent.getStringExtra("body"));
            args.put("group_id", intent.getStringExtra("contact_id"));
            args.put("type","normal");

            socket.emit("group-message",args);
            Log.d("Send-Group","Id: "+args.getLong("local_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void createGroup(Intent intent){

        try {
            ArrayList<ParcelContacts> Contacts = intent.getParcelableArrayListExtra("Contacts");
            Contacts.add(new ParcelContacts(user.ID, user.Name, "","")); //Add myself to group
            JSONArray array = new JSONArray();

            for(int i=0; i< Contacts.size(); i++){
                JSONObject o = new JSONObject();
                o.put("user_id", Contacts.get(i).Contact_ID);
                array.put(i, o);
            }

            JSONObject args = new JSONObject();
            args.put("creator", user.ID);
            args.put("name", intent.getStringExtra("GroupName"));
            args.put("members", array);

            socket.emit("group-create", args);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void addNewGroupContact(JSONObject data){

        try {
            Contact contact = new Contact();
            contact.Contact_Id = data.getString("group_id");
            contact.Join_Date = data.getString("datetime");
            contact.Name = data.getString("name");
            contact.Type = Constants.CONTACT_TYPE_GROUP;

            Log.d("AddedToNewGroup", contact.Name);
            if(!db.contactExists(contact.Contact_Id)){
                db.insertContact(contact);

                Message message = new Message();
                message.Contact_Id = contact.Contact_Id;
                message.Message_Id= new Date().toString();
                Log.d("MessageId", message.Message_Id);
                message.From = contact.Contact_Id;
                message.Body = "New group created";
                message.Type = "group-create";
                message.Category = Constants.CATEGORY_GROUP_MESSAGE;
                message.PostedTime = contact.Join_Date;

                db.insertMessage(message, Constants.MESSAGE_RECEIVE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void AddMembersToGroup(Intent intent){
        ArrayList<ParcelContacts> Contacts = intent.getParcelableArrayListExtra("Contacts");

        try {

            JSONObject arg = new JSONObject();
            JSONArray members = new JSONArray();

            for(int i=0; i<Contacts.size(); i++){
                JSONObject user = new JSONObject();
                user.put("user_id", Contacts.get(i).Contact_ID);
                members.put(i, user);
            }

            arg.put("group_id", intent.getStringExtra("group_id"));
            arg.put("members", members);

            db.insertGroupMap(intent.getStringExtra("group_id"), Tools.getGroupMemberFromSelectedContacts(getApplicationContext(),Contacts));
            socket.emit("add-group-members", arg);
            Toast.makeText(getApplicationContext(),"Member was added to the group",Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void RemoveMembersFromGroup(Intent intent){
        String contact_id = intent.getStringExtra("contact_id");
        String group_id = intent.getStringExtra("group_id");

        ArrayList<GroupMember> Members = new ArrayList<>();
        GroupMember member = new GroupMember();
        member.Contact_Id = contact_id;

        try {
            JSONArray array = new JSONArray();
            JSONObject users = new JSONObject();

            users.put("user_id", contact_id);
            array.put(0,users);

            JSONObject arg = new JSONObject();
            arg.put("user_id", user.ID);
            arg.put("group_id", group_id);
            arg.put("members", array);

            Members.add(member);
            db.deleteGroupMap(group_id, Members);

            socket.emit("remove-group-members", arg);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void exitGroup(Intent intent){
        String group_id = intent.getStringExtra("group_id");
        try{
            JSONArray array = new JSONArray();
            JSONObject users = new JSONObject();

            users.put("user_id", user.ID);
            array.put(0,users);

            JSONObject arg = new JSONObject();
            arg.put("user_id", user.ID);
            arg.put("group_id", group_id);
            arg.put("members", array);

            if(socket.connected()){
                socket.emit("remove-group-members", arg);

                ArrayList<GroupMember> Members = new ArrayList<>();
                GroupMember member = new GroupMember();
                member.Contact_Id = user.ID;
                Members.add(member);

                db.deleteGroupMap(group_id,Members);
                db.deleteContact(group_id);
                db.deleteMessageView(group_id);
                broadcastGroupExited(group_id, Constants.GROUP_EXIT_SUCCESSFULL);
            }else{
                broadcastGroupExited(group_id, Constants.GROUP_EXIT_ERROR);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void initializeUser(){
        user = db.getUser();
        if(user.ID == null){
            Tools.smallNotification(getApplicationContext(),"Stopping service","User data empty");
            stopSelf();
        }
    }

    void broadcastGroupExited(String group_id, int status){
        Intent intent = new Intent();
        intent.setAction(Constants.GROUP_EXIT_FILTER);
        intent.putExtra("group_id", group_id);
        intent.putExtra("status", status);

        sendBroadcast(intent);
    }
}
