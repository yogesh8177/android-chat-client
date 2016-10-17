package yogispark.chat;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import yogispark.chat.Fragments.ContainerFragment;
import yogispark.chat.Service.ChatService;
import yogispark.chat.Service.SyncContacts;
import yogispark.chat.UI.ContactSelect;
import yogispark.chat.UI.Register;
import yogispark.chat.Utility.Constants;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Intent chatService,register;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        startChatService();
        syncContacts();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_new_group){
            Intent intent = new Intent(this, ContactSelect.class);
            intent.putExtra("CONTACT_SELECT_FOR", Constants.CONTACT_SELECT_FOR_NEW_GROUP); //CONTACT_SELECT_FOR_NEW_GROUP gets al private contacts
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, new ContainerFragment())
                    .commit();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void syncContacts(){
        Intent sync = new Intent(MainActivity.this, SyncContacts.class);
        startService(sync);
//        ComponentName componentName = new ComponentName(this, SyncContacts.class);
//        JobInfo jobInfo = new JobInfo.Builder(Constants.SYNC_JOB_ID, componentName)
//                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                                    .setRequiresCharging(false)
//                                    .setRequiresDeviceIdle(true)
//                                    .setPeriodic(1000*60*2)
//                                    .setPersisted(true)
//                                    .build();
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        int result = scheduler.schedule(jobInfo);
//        if(result == JobScheduler.RESULT_SUCCESS) Log.d("Scheduler","Scheduled");
    }

    void startChatService(){
        boolean isRegistered = getApplicationContext().getSharedPreferences("Registration",0).getBoolean("Registered",false);
        if(isRegistered){
            chatService = new Intent(this, ChatService.class);
            startService(chatService);
        }else{
            register = new Intent(MainActivity.this, Register.class);
            startActivity(register);
        }

    }
}
