package yogispark.chat.UI;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import yogispark.chat.Fragments.ContactSelectFragment;
import yogispark.chat.R;

public class ContactSelect extends AppCompatActivity {

    FragmentManager fragmentManager;
    public static String GROUP_ID;
    public static int CONTACT_SELECT_FOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fragmentManager = getSupportFragmentManager();
        CONTACT_SELECT_FOR = getIntent().getIntExtra("CONTACT_SELECT_FOR",0);
        GROUP_ID = getIntent().hasExtra("GroupId") ? getIntent().getStringExtra("GroupId") : null;

        if(savedInstanceState == null){
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, new ContactSelectFragment())
                    .commit();
        }
    }

}
