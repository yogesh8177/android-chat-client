package yogispark.chat.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import yogispark.chat.Asynctasks.ResetMessageCount;
import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Fragments.ChatViewFragment;
import yogispark.chat.Models.User;
import yogispark.chat.R;
import yogispark.chat.Utility.Constants;

public class ChatView extends AppCompatActivity {

    public static String CONTACT_ID;
    public static int Category;
    public static String CURRENT_USER;
    public static String CONTACT_NAME;

    FragmentManager fragmentManager;
    User user;
    SqlHelper helper;
    Button Contact_Info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setToolBar(this, toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();
        if(savedInstanceState==null){

            helper = new SqlHelper(getApplicationContext());
            user = helper.getUser();
            CURRENT_USER = user.ID;
            Category = getIntent().getIntExtra("category",0);
            CONTACT_ID = getIntent().getStringExtra("contact_id");
            CONTACT_NAME = getIntent().getStringExtra("name");

            fragmentManager.beginTransaction()
                    .replace(R.id.container, new ChatViewFragment())
                    .commit();

            new ResetMessageCount(this,CONTACT_ID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            setTitle(Category == Constants.CATEGORY_GROUP_MESSAGE ? "Group: "+CONTACT_NAME : CONTACT_NAME);
        }else{
            Category = savedInstanceState.getInt("Category");
            CONTACT_ID = savedInstanceState.getString("Contact_Id");
            CONTACT_NAME = savedInstanceState.getString("Contact_Name");
            setTitle(Category == Constants.CATEGORY_GROUP_MESSAGE ? "Group: "+CONTACT_NAME : CONTACT_NAME);
        }
        Log.d("CONTACT",CONTACT_ID);
    }

    void setToolBar(final Context context, Toolbar toolbar){

        Contact_Info = new Button(context);
        Contact_Info.setBackgroundColor(Color.TRANSPARENT);
        Drawable icon = getDrawable(R.drawable.ic_action_info);
        icon.setBounds(0,0,120,120);
        Contact_Info.setCompoundDrawables(icon,null,null,null);
        Contact_Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Category == Constants.CATEGORY_GROUP_MESSAGE){
                    Intent group_info = new Intent(ChatView.this, GroupInfo.class);
                    group_info.putExtra("Name", CONTACT_NAME);
                    group_info.putExtra("GroupId", CONTACT_ID);
                    startActivity(group_info);
                }else if(Category == Constants.CATEGORY_PRIVATE_MESSAGE){
                    Intent member_info = new Intent(ChatView.this, MemberInfo.class);
                    member_info.putExtra("contact_id", CONTACT_ID);
                    startActivity(member_info);
                }
            }
        });
        toolbar.addView(Contact_Info);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("Contact_Id", CONTACT_ID);
        outState.putInt("Category", Category);
        outState.putString("Contact_Name", CONTACT_NAME);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        CONTACT_ID = null;
        Category = 0;
        super.onDestroy();
    }
}
