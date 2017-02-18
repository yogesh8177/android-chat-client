package yogispark.chat.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import yogispark.chat.Asynctasks.FetchUser;
import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.User;
import yogispark.chat.R;
import yogispark.chat.Utility.Constants;

public class UserSetting extends AppCompatActivity implements LoaderManager.LoaderCallbacks<User>{

    EditText status_input;
    Button update_button;
    boolean isLoading = false;
    String User_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        status_input = (EditText) findViewById(R.id.status_input);
        update_button = (Button) findViewById(R.id.status_update_button);

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status_input.getText().length() != 0)
                    sendStatusUpdateBroadcast(status_input.getText().toString());
            }
        });
        if(!isLoading)
            getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        else
            getSupportLoaderManager().initLoader(0, null, this);
    }

    void sendStatusUpdateBroadcast(String status){

        new BackgroundTask(status, User_Id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public Loader<User> onCreateLoader(int id, Bundle args) {
        isLoading = true;
        return new FetchUser(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<User> loader, User data) {
        isLoading = false;
        status_input.setText(data.Status);
        User_Id = data.ID;
    }

    @Override
    public void onLoaderReset(Loader<User> loader) {

    }

    class BackgroundTask extends AsyncTask<Void, Void, Void>{

        String Status, Contact_Id;

        public BackgroundTask(String status, String contact_id){
            Status = status;
            Contact_Id = contact_id;
        }
        @Override
        protected Void doInBackground(Void... params) {
            SqlHelper helper = new SqlHelper(getApplicationContext());
            helper.updateUserStatus(Contact_Id, Status);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().putBoolean("status_update", true).commit();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent broadcast = new Intent();
            broadcast.setAction(Constants.UPDATE_USER_SETTING_FILTER);
            broadcast.putExtra("category", Constants.UPDATE_USER_STATUS);
            broadcast.putExtra("contact_id", User_Id);
            broadcast.putExtra("status", Status);

            sendBroadcast(broadcast);
        }

    }
}
