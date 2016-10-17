package yogispark.chat.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import yogispark.chat.Models.Contact;
import yogispark.chat.Models.GroupMember;
import yogispark.chat.Models.Message;
import yogispark.chat.Models.MessageView;
import yogispark.chat.Models.User;
import yogispark.chat.Utility.Constants;
import yogispark.chat.Utility.Tools;

/**
 * Created by yogesh on 3/10/16.
 */
public class SqlHelper extends SQLiteOpenHelper {

    final static int VERSION = 1;
    public final static int MESSAGE_POSTED = 1;
    public final static int MESSAGE_DELIVERED = 2;
    public final static int MESSAGE_READ = 3;

    public final static int MESSAGE_PRIVATE = 1;
    public final static int MESSAGE_GROUP = 2;

    public final static int MESSAGE_VIEW_INCREMENT = 1;
    public final static int MESSAGE_VIEW_DECREMENT = 2;
    public final static int MESSAGE_VIEW_NO_INCREMENT = 3;

    final static String DATABASE_NAME = "ChatDB";
    final static String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS user" +
                                            "(user_id TEXT PRIMARY KEY," +
                                            "name TEXT," +
                                            "email TEXT," +
                                            "mobile TEXT," +
                                            "token TEXT)";
    final static String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS message " +
                                                "(local_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                "message_id TEXT," +
                                                "contact_id TEXT,"+
                                                "body TEXT," +
                                                "type TEXT," +
                                                "category INTEGER,"+
                                                "datetime TEXT," +
                                                "delivery_time TEXT," +
                                                "read_time TEXT," +
                                                "sender TEXT," +
                                                "require_push INTEGER)";

    final static String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS contacts " +
                                                "(contact_id TEXT PRIMARY KEY," +
                                                "type TEXT," +
                                                "name TEXT," +
                                                "join_date TEXT," +
                                                "status TEXT)";

    final static String CREATE_MESSAGE_VIEW_TABLE = "CREATE TABLE IF NOT EXISTS message_view " +
                                                    "(contact_id TEXT PRIMARY KEY," +
                                                    "message_id TEXT," +
                                                    "count INTEGER)";

    final static String CREATE_GROUP_MAP_TABLE = "CREATE TABLE IF NOT EXISTS group_map " +
                                                "(group_id TEXT," +
                                                "contact_id TEXT," +
                                                "join_date TEXT)";

    final static String CREATE_GROUP_MESSAGE_META_TABLE = "CREATE TABLE IF NOT EXISTS group_message_meta " +
            "(group_id TEXT NOT NULL," +
            "contact_id TEXT NOT NULL," +
            "message_id TEXT NOT NULL,"+
            "delivery_date TEXT," +
            "read_date TEXT)";


    final static String DROP_USER_TABLE = "DROP TABLE IF EXISTS user";
    final static String DROP_MESSAGE_TABLE = "DROP TABLE IF EXISTS message";
    final static String DROP_CONTACTS_TABLE = "DROP TABLE IF EXISTS contacts";
    final static String DROP_MESSAGE_VIEW_TABLE = "DROP TABLE IF EXISTS message_view";
    final static String DROP_GROUP_MAP_TABLE = "DROP TABLE IF EXISTS group_map";
    final static String DROP_GROUP_MESSAGE_META_TABLE = "DROP TABLE IF EXISTS group_message_meta";

    Context context;

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL(CREATE_MESSAGE_VIEW_TABLE);
        db.execSQL(CREATE_GROUP_MAP_TABLE);
        db.execSQL(CREATE_GROUP_MESSAGE_META_TABLE);
        db.execSQL(CREATE_USER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_CONTACTS_TABLE);
        db.execSQL(DROP_MESSAGE_TABLE);
        db.execSQL(DROP_MESSAGE_VIEW_TABLE);
        db.execSQL(DROP_GROUP_MAP_TABLE);
        db.execSQL(DROP_GROUP_MESSAGE_META_TABLE);
        onCreate(db);
    }

    //######################### User ##########################
    public long insertUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", user.ID);
        values.put("name", user.Name);
        values.put("email", user.Email);
        values.put("mobile", user.Mobile);
        values.put("token", user.Token);

        return db.insert("user",null,values);
    }

    public long updateMobile(String user_id, String mobile){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("mobile", mobile);

        return db.update("user",values,"user_id = ?", new String[]{user_id});
    }

    public User getUser(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("user",new String[]{"user_id","mobile","token","name","email"},null,null,null,null,null);
        c.moveToFirst();

        User user = new User();
        user.ID = c.getString(0);
        user.Mobile = c.getString(1);
        user.Token = c.getString(2);
        user.Name = c.getString(3);
        user.Email = c.getString(4);

        return user;
    }

    //############################# Message ###########################################
    public long insertMessage(Message message, int type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("message_id", message.Message_Id);
        values.put("contact_id", message.Contact_Id);
        values.put("body", message.Body);
        values.put("type", message.Type);
        values.put("datetime", message.PostedTime);
        values.put("sender", message.From); //id of sender
        values.put("category",message.Category);
        values.put("require_push", message.Require_Push);

//initializeMessageView(message.Contact_Id);
        if(type == Constants.MESSAGE_RECEIVE)
            updateMessageView(message.Contact_Id, message.Message_Id, MESSAGE_VIEW_INCREMENT);

        return db.insert("message", null,values);
    }

    public void deleteMessage(String message_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("message","message_id LIKE ?", new String[]{message_id});

    }

    public void updateMessage(Message message, int type){
        SQLiteDatabase db = this.getWritableDatabase();

        switch (type){
            case MESSAGE_POSTED:
                ContentValues values_posted = new ContentValues();
                values_posted.put("message_id", message.Message_Id);
                values_posted.put("datetime", message.PostedTime);
                values_posted.put("require_push",0);
                long rows = db.update("message", values_posted, "local_id LIKE ?", new String[]{(String.valueOf(message.Local_Id))});
                Log.d("Row updated",""+rows);
                break;

            case MESSAGE_DELIVERED:
                ContentValues values_delivered = new ContentValues();
                values_delivered.put("delivery_time", message.DeliveryTime);
                long row = db.update("message", values_delivered, "message_id LIKE ?", new String[]{(message.Message_Id)});
                Log.d("Delivered",""+row);
                break;

            case MESSAGE_READ:
                ContentValues values_read = new ContentValues();
                values_read.put("read_time", message.ReadTime);
                db.update("message", values_read, "message_id LIKE ?", new String[]{(message.Message_Id)});
                break;

            default:

                break;
        }

    }

    public ArrayList<Message> getMessages(String contact_id, int type){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Message> Result = new ArrayList<>();
        Cursor c = null;
        if(type == Constants.CATEGORY_PRIVATE_MESSAGE){
            c = db.query("message",new String[]{"local_id","message_id","body","type","datetime","delivery_time","read_time","category"},"category LIKE "+type+" AND contact_id = ?",new String[]{contact_id},null,null,null);
        }else if(type == Constants.CATEGORY_GROUP_MESSAGE){
            c = db.rawQuery("SELECT message.local_id, message.message_id, message.body, message.type, message.datetime, message.delivery_time, message.read_time, message.category, contacts.name, contacts.status FROM message LEFT JOIN contacts ON message.sender = contacts.contact_id WHERE message.contact_id = ?",new String[]{contact_id});
        }

//Log.d("Contact ID: ",contact_id+", "+c.getCount());
        if(c!= null){
            while (c.moveToNext()){
                Message message = new Message();
                message.Local_Id = c.getInt(0);
                message.Message_Id = c.getString(1);
                message.Body = c.getString(2);
                message.Type = c.getString(3);
                message.PostedTime = c.getString(4);
                message.DeliveryTime = c.getString(5);
                message.ReadTime = c.getString(6);
                message.Category = c.getInt(7);
                message.Contact_Name = type == Constants.CATEGORY_GROUP_MESSAGE ? c.getString(8) : "";
                message.Contact_Status = type == Constants.CATEGORY_GROUP_MESSAGE ? c.getString(9) : "";

                Result.add(message);
            }

        }

        return Result;
    }

    //############################## Contacts #########################################
    public long insertContact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("contact_id", contact.Contact_Id);
        values.put("type", contact.Type);
        values.put("name", contact.Name);
        values.put("join_date", contact.Join_Date);
        values.put("status", contact.Status);
Log.d("Contact added", contact.Name);
        long row_id = db.insert("contacts", null,values);
        long init_id = initializeMessageView(contact.Contact_Id); //initialize message view with contact_id and count defaulting to 0

        return row_id & init_id;
    }

    public void deleteContact(String contact_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("contacts","contact_id LIKE ?",new String[]{contact_id});
    }

    public void updateContact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", contact.Name);
        values.put("status", contact.Status);

        db.update("contacts", values,"contact_id LIKE ?", new String[]{contact.Contact_Id});

    }
//Get contact list
    public ArrayList<Contact> getContacts(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Contact> Result = new ArrayList<>();

        Cursor c = db.query("contacts",new String[]{"contact_id","name","status","join_date"},"type = ?",new String[]{"private"},null,null,null);

        if(c!=null){
            while (c.moveToNext()){
                Contact contact = new Contact();
                contact.Contact_Id = c.getString(0);
                contact.Name = c.getString(1);
                contact.Status = c.getString(2);
                contact.Join_Date = c.getString(3);

                Result.add(contact);
            }

        }

        return Result;
    }

    public ArrayList<Contact> getContactsNotInGroup(String group_id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Contact> Result = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT contacts.contact_id, contacts.name, contacts.status, contacts.status FROM contacts WHERE contacts.type = ? AND contacts.contact_id NOT IN(SELECT group_map.contact_id FROM group_map WHERE group_map.group_id = ?)", new String[]{"private", group_id});
        if(c!=null){
            while (c.moveToNext()){
                Contact contact = new Contact();
                contact.Contact_Id = c.getString(0);
                contact.Name = c.getString(1);
                contact.Status = c.getString(2);
                contact.Join_Date = c.getString(3);

                Result.add(contact);
            }

        }

        return Result;
    }


    public int totalContacts(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("contacts",new String[]{"contact_id"},"type LIKE ?",new String[]{Constants.CONTACT_TYPE_PRIVATE},null,null,null);
        return c.getCount();
    }
//Check if contact exists
    public boolean contactExists(String contact_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("contacts",new String[]{"contact_id"},"contact_id = ?", new String[]{contact_id}, null, null, null);

        return c.getCount() == 0 ? false : true;
    }

    public String getContactId(String message_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("message",new String[]{"contact_id"},"message_id = ?", new String[]{message_id},null,null,null);
        c.moveToFirst();
        return c.getString(0);
    }

    public String getContactName(String contact_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("contacts",new String[]{"name"},"contact_id = ?", new String[]{contact_id},null,null,null);

        return c.moveToFirst() == false ? "Unknown" : c.getString(0);
    }

    //############################### Message View ################################
    public long initializeMessageView(String contact_id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("contact_id", contact_id);
        values.put("count", 0);

        return db.insert("message_view", null,values);
    }
//Get list of latest messages
    public ArrayList<MessageView> getMessageView(){
        ArrayList<MessageView> Result = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT message_view.contact_id, message_view.message_id, message_view.count, message.body, message.datetime, message.category, contacts.name FROM message_view INNER JOIN message ON message_view.message_id = message.message_id INNER JOIN contacts ON message_view.contact_id = contacts.contact_id", new String[]{});
        Log.d("messages: ",""+c.getCount());
        while (c.moveToNext()){
            MessageView mv = new MessageView();
            mv.Contact_Id = c.getString(0);
            mv.Message_Id = c.getString(1);
            mv.Count = c.getInt(2);
            mv.Body = c.getString(3);
            mv.Posted_Date = c.getString(4);
            mv.Category = c.getInt(5);
            mv.Contact_Name = c.getString(6);

            Result.add(mv);
        }

        return Result;
    }

    public void deleteMessageView(String contact_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("message_view","contact_id LIKE ?",new String[]{contact_id});

    }

    public void updateMessageView(String contact_id, String message_id, int increment){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("MessageView",""+message_id);
        switch (increment){
            case MESSAGE_VIEW_INCREMENT:
                db.execSQL("UPDATE message_view SET message_id = ?, count = count + 1 WHERE contact_id = ?", new String[]{message_id, contact_id});
                break;

            case MESSAGE_VIEW_DECREMENT:
                db.execSQL("UPDATE message_view SET message_id = ?, count = count - 1 WHERE contact_id = ?", new String[]{message_id, contact_id});
                break;

            case MESSAGE_VIEW_NO_INCREMENT:
                db.execSQL("UPDATE message_view SET message_id = ? WHERE contact_id = ?", new String[]{message_id, contact_id});
                break;
            default:

                break;

        }

    }

    public void resetCountMessageView(String contact_id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("count",0);
        db.update("message_view",values,"contact_id LIKE ?", new String[]{contact_id});

    }


    //############################# Group Map ######################################
    public void insertGroupMap(String group_id, ArrayList<GroupMember> members){
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db2 = this.getReadableDatabase();

        db.beginTransaction();

        for(GroupMember member: members){

            ContentValues values = new ContentValues();
            values.put("group_id", group_id);
            values.put("contact_id", member.Contact_Id);
            values.put("join_date", member.Join_date);

            Cursor c = db2.query("group_map",new String[]{"contact_id"},"group_id = ? AND contact_id = ?",new String[]{group_id, member.Contact_Id},null,null,null);

            if(c.getCount() == 0){ //check if already such member exists or not
                long row = db.insert("group_map", null,values);
                Log.d("Group-Map", ""+row+", "+member.Contact_Id);
                Tools.smallNotification(context,member.Contact_Id,"Added to group");
            }else{
                Log.d("Group-Map", "Already exists");
            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public void deleteGroupMap(String group_id, ArrayList<GroupMember> members){
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        for(GroupMember member:members){
           long row = db.delete("group_map","contact_id = ? AND group_id = ?",new String[]{member.Contact_Id, group_id});
            Log.d("Group-Delete-Map", ""+row+", Group: "+group_id+ ", Member: "+member.Contact_Id);
        }

        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public ArrayList<Contact> getGroupMembers(String group_id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Contact> Result = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT contacts.contact_id, contacts.name, contacts.status, group_map.join_date FROM group_map LEFT JOIN contacts ON group_map.contact_id = contacts.contact_id WHERE group_map.group_id = ?", new String[]{group_id} );
        while (c.moveToNext()){
            Contact gm = new Contact();
            gm.Contact_Id = c.getString(0);
            gm.Name = c.getString(1);
            gm.Status = c.getString(2);
            gm.Join_Date = c.getString(3);

            Result.add(gm);
        }

        return Result;
    }

    public void addGroupMessageMeta(Message message){

        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db1 = this.getReadableDatabase();
//Get group id (contact_id) from message table from the given message_id
        Cursor c = db1.query("message",new String[]{"contact_id"},"message_id LIKE ?", new String[]{message.Message_Id},null,null,null);
        c.moveToFirst();
        ContentValues values = new ContentValues();

        values.put("message_id", message.Message_Id);
        values.put("group_id", c.getString(0));
        values.put("contact_id", message.From);
        values.put("delivery_date", message.DeliveryTime);

        long row = db.insert("group_message_meta",null,values);
        Log.d("Group meta inserted",""+row);
    }



}
