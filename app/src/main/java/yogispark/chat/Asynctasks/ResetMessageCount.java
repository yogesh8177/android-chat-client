package yogispark.chat.Asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.Contact;

/**
 * Created by yogesh on 7/10/16.
 */
public class ResetMessageCount extends AsyncTask<Void, Void, Void> {

    Context context;
    String CONTACT_ID;

    public ResetMessageCount(Context context, String contact_id){
        this.context = context;
        CONTACT_ID = contact_id;
    }

    @Override
    protected Void doInBackground(Void... params) {
        SqlHelper helper = new SqlHelper(context);
        helper.resetCountMessageView(CONTACT_ID);

        return null;
    }
}
