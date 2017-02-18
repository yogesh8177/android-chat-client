package yogispark.chat.Asynctasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Dialogs.MessageDetailsDialog;
import yogispark.chat.Models.Contact;

/**
 * Created by yogesh on 18/10/16.
 */
public class FetchMessageDetails extends AsyncTaskLoader<ArrayList<Contact>> {

    int type;
    String Message_Id;
    ArrayList<Contact> Result;
    SqlHelper helper;

    public FetchMessageDetails(Context context, String message_id,int type) {
        super(context);
        this.type = type;
        Message_Id = message_id;
    }

    @Override
    public ArrayList<Contact> loadInBackground() {

        helper = new SqlHelper(getContext());
        Result = helper.getGroupMessageMeta( Message_Id, type);

        return Result;
    }
}
