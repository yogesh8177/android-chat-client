package yogispark.chat.Asynctasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.Contact;

/**
 * Created by yogesh on 22/10/16.
 */
public class FetchContactInfo extends AsyncTaskLoader<Contact> {

    String Contact_Id;

    public FetchContactInfo(Context context, String contact_id) {
        super(context);
        Contact_Id = contact_id;
    }

    @Override
    public Contact loadInBackground() {
        SqlHelper helper = new SqlHelper(getContext());
        return helper.getContactInfo(Contact_Id);
    }
}
