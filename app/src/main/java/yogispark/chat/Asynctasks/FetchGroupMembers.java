package yogispark.chat.Asynctasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.Contact;
import yogispark.chat.Models.GroupMember;

/**
 * Created by yogesh on 17/10/16.
 */
public class FetchGroupMembers extends AsyncTaskLoader<ArrayList<Contact>> {

    SqlHelper helper;
    String Group_Id;

    public FetchGroupMembers(Context context, String group_id) {
        super(context);
        this.Group_Id = group_id;
    }

    @Override
    public ArrayList<Contact> loadInBackground() {

        helper = new SqlHelper(getContext());
        ArrayList<Contact> Members = helper.getGroupMembers(Group_Id);

        return Members;
    }
}
