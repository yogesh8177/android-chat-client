package yogispark.chat.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import yogispark.chat.Models.User;
import yogispark.chat.R;
import yogispark.chat.Service.ChatService;
import yogispark.chat.Service.RegisterService;
import yogispark.chat.Utility.Constants;

public class Register extends AppCompatActivity {

    EditText user_name, email, mobile, password;
    Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user_name = (EditText) findViewById(R.id.user_name);
        email = (EditText) findViewById(R.id.email);
        mobile = (EditText) findViewById(R.id.mobile);
        password = (EditText) findViewById(R.id.password);
        register_button = (Button) findViewById(R.id.register_button);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    Intent registerService = new Intent(Register.this, RegisterService.class);
                    registerService.putExtra("Name", user_name.getText().toString());
                    registerService.putExtra("Email", email.getText().toString());
                    registerService.putExtra("Mobile", mobile.getText().toString());
                    registerService.putExtra("Password", password.getText().toString());

                    startService(registerService);
                }
            }
        });

        BroadcastReceiver receiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getBooleanExtra("Registered",false)){
                    Toast.makeText(getApplicationContext(),"Successfully registered!", Toast.LENGTH_SHORT).show();
                    startChatService();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Could not register, try again!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.USER_REGISTERED_FILTER);
        registerReceiver(receiver, filter);
    }

    boolean isValid(){
        boolean result = true;

        if(user_name.getText().length() == 0){
            result = false;
        }
        if(email.getText().length() == 0){
            result = false;
        }
        if(mobile.getText().length() == 0){
            result = false;
        }
        if(password.getText().length() == 0){
            result = false;
        }

        return result;
    }

    void startChatService(){
        Intent chatService = new Intent(this, ChatService.class);
        startService(chatService);
    }
}
