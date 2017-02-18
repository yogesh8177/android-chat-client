package yogispark.chat.Asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Utility.Constants;

/**
 * Created by yogesh on 10/10/16.
 */
public class UpdateMessageView extends AsyncTask<String, Void, Void> {

    Context context;
    public UpdateMessageView(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(String... params) {
        SqlHelper helper = new SqlHelper(context);
        helper.updateMessageView(params[0], params[1], 0l, SqlHelper.MESSAGE_VIEW_INCREMENT);
        Log.d("MV","ID:"+params[1]);
        return null;
    }
}
