package yogispark.chat.Service;

import android.app.IntentService;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.Contact;
import yogispark.chat.Utility.Constants;
import yogispark.chat.Utility.Tools;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class FetchContacts extends IntentService {

    HttpURLConnection connection;
    SqlHelper helper;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    public FetchContacts() {
        super("FetchContacts");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        powerManager = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"lock");
        wakeLock.acquire();

        try{
            String parameter = "?datetime=" + getLatestContactDateTime();
            helper = new SqlHelper(getApplicationContext());

            connection = (HttpURLConnection) new URL(Constants.FETCH_CONTACTS_URL+parameter).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type","text/plain");


            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine())!=null){
                builder.append(line);
            }
            JSONArray array = new JSONArray(builder.toString());
            int size = array.length();

            for(int i=0; i<size; i++){
                JSONObject o = array.getJSONObject(i);

                Contact contact = new Contact();
                contact.Contact_Id = o.getString("_id");
                contact.Name = o.getString("name");
                contact.Join_Date = o.getString("datetime");
                contact.Type = Constants.CONTACT_TYPE_PRIVATE;
                contact.Status = o.getString("status");

                helper.insertContact(contact);
                if(i == size-1) //If last contact having latest join date
                    addLatestContactDateTime(contact.Join_Date);
            }

            Tools.smallNotification(getApplicationContext(),size+" New contacts were added","New Contacts");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            connection.disconnect();
            wakeLock.release();
        }
    }

    void addLatestContactDateTime(String datetime){
        getApplicationContext().getSharedPreferences("LatestContact",0).edit().putString("datetime",datetime).commit();
    }

    String getLatestContactDateTime(){
       return getApplicationContext().getSharedPreferences("LatestContact",0).getString("datetime","");
    }


}
