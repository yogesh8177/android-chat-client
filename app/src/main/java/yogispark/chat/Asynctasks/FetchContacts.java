package yogispark.chat.Asynctasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.Contact;
import yogispark.chat.UI.ContactSelect;
import yogispark.chat.Utility.Constants;

/**
 * Created by yogesh on 14/10/16.
 */
public class FetchContacts extends AsyncTaskLoader<ArrayList<Contact>> {

    Context context;
    String group_id;
    int type;

    public FetchContacts(Context context, int type){
        super(context);
        this.context = context;
        this.type = type;

    }

    @Override
    public ArrayList<Contact> loadInBackground() {


        SqlHelper helper = new SqlHelper(context);

        if(type == Constants.CONTACT_SELECT_FOR_NEW_GROUP){
            ArrayList<Contact> Contacts = helper.getContacts();
            return Contacts;
        }else{
            ArrayList<Contact> Contacts = helper.getContactsNotInGroup(ContactSelect.GROUP_ID);
            return Contacts;
        }


    }
}
