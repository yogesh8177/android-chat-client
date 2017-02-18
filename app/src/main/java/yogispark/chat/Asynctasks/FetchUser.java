package yogispark.chat.Asynctasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.User;

/**
 * Created by yogesh on 23/10/16.
 */
public class FetchUser extends AsyncTaskLoader<User> {
    public FetchUser(Context context) {
        super(context);
    }

    @Override
    public User loadInBackground() {
        SqlHelper helper = new SqlHelper(getContext());
        return helper.getUser();
    }
}
