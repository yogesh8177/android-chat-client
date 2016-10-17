package yogispark.chat.Service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

/**
 * Created by yogesh on 15/10/16.
 */
public class BackgroundSync extends JobService {


    @Override
    public boolean onStartJob(JobParameters params) {

        Intent sync = new Intent(getApplicationContext(),SyncContacts.class);
        startService(sync);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
