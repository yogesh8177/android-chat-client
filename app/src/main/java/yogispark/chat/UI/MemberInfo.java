package yogispark.chat.UI;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import yogispark.chat.Asynctasks.FetchContactInfo;
import yogispark.chat.Models.Contact;
import yogispark.chat.R;
import yogispark.chat.Utility.Tools;

public class MemberInfo extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Contact>{

    String Contact_Id;
    TextView contact_name, contact_join_date, contact_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Contact_Id = getIntent().getStringExtra("contact_id");

        contact_name = (TextView) findViewById(R.id.contact_name);
        contact_join_date = (TextView) findViewById(R.id.contact_join_date);
        contact_status = (TextView) findViewById(R.id.contact_status);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<Contact> onCreateLoader(int id, Bundle args) {

        return new FetchContactInfo(getApplicationContext(), Contact_Id);
    }

    @Override
    public void onLoadFinished(Loader<Contact> loader, Contact data) {
        prepareUI(data);
    }

    @Override
    public void onLoaderReset(Loader<Contact> loader) {

    }

    private void prepareUI(Contact contact) {
        contact_name.setText(contact.Name);
        contact_join_date.setText(Tools.parseISODate(contact.Join_Date));
        contact_status.setText(contact.Status);
    }
}
