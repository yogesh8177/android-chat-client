package yogispark.chat.Service;

import android.app.IntentService;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Utility.Constants;
import yogispark.chat.Utility.Tools;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class SyncContacts extends IntentService {

    HttpURLConnection connection;
    SqlHelper helper;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    public SyncContacts() {
        super("SyncContacts");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        powerManager = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"lock");
        wakeLock.acquire();

        try{
            helper = new SqlHelper(getApplicationContext());
            connection = (HttpURLConnection) new URL(Constants.SYNC_CONTACTS).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");


            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine())!=null){
                builder.append(line);
            }
            JSONObject object = new JSONObject(builder.toString());
            notifyContacts(object.getInt("count"));


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            connection.disconnect();
            wakeLock.release();
        }
    }

    void notifyContacts(int count){
        int total = count - helper.totalContacts();Log.d("Total",""+total);
        if(total > 0){
            Log.d("Sync",""+total);
            Tools.smallNotification(getApplicationContext(),""+total+" new contacts are waiting","New Contacts");
            fetchContacts();
        }
    }

    void fetchContacts(){
        Intent fetch = new Intent(SyncContacts.this, FetchContacts.class);
        startService(fetch);
    }

}
