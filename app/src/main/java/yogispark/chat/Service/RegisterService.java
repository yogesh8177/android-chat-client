package yogispark.chat.Service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.User;
import yogispark.chat.Utility.Constants;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RegisterService extends IntentService {

    HttpURLConnection connection;
    String parameters;
    SqlHelper helper;
    String name,email,mobile,password;

    public RegisterService() {
        super("RegisterService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try{
            name = intent.getStringExtra("Name");
            email = intent.getStringExtra("Email");
            mobile = intent.getStringExtra("Mobile");
            password = intent.getStringExtra("Password");

            parameters = "username="+name+"&email="+email+"&mobile="+mobile+"&password="+password;
            connection = (HttpURLConnection) new URL(Constants.REGISTER_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
            writer.write(parameters);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine())!=null){
                builder.append(line);
            }
            JSONObject data = new JSONObject(builder.toString());

            if(data.has("error")){
                Log.d("Registration error",data.getString("error"));
                sendBroadcast(false); //Send broadcast to convey registration failed
            }else{
                insertUserDetails(data);
            }


        }catch (Exception e){
            Log.d("Register-Exception",e.getMessage());
        }
    }

    void insertUserDetails(JSONObject object){

        helper = new SqlHelper(getApplicationContext());
        User user = new User();
        try {
            user.ID = object.getString("user_id");
            user.Token = object.getString("token");
            user.Name = name;
            user.Email = email;
            user.Mobile = mobile;

            if(helper.insertUser(user) != -1){
                getApplicationContext().getSharedPreferences("Registration",0).edit().putBoolean("Registered", true).commit();
                sendBroadcast(true);
            }else{
                getApplicationContext().getSharedPreferences("Registration",0).edit().putBoolean("Registered", false).commit();
                sendBroadcast(false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void sendBroadcast(boolean status){
        Intent intent = new Intent();
        intent.setAction(Constants.USER_REGISTERED_FILTER);
        intent.putExtra("Registered", status);
        sendBroadcast(intent);
    }


}
