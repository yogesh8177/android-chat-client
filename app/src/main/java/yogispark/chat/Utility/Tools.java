package yogispark.chat.Utility;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Date;

import yogispark.chat.DataBase.SqlHelper;
import yogispark.chat.Models.GroupMember;
import yogispark.chat.Models.Parcelable.ParcelContacts;
import yogispark.chat.Models.User;
import yogispark.chat.R;

/**
 * Created by yogesh on 5/10/16.
 */
public class Tools {

    public static void smallNotification(final Context context,final String body, final String title){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationCompat = new  NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_menu_send)
                        .setContentText(body)
                        .setContentTitle(title)
                        .setAutoCancel(true);

                notificationManager.notify(1,notificationCompat.build());
            }
        });

    }

    public static ArrayList<GroupMember> getGroupMemberFromSelectedContacts(Context context,ArrayList<ParcelContacts> Contacts){
        ArrayList<GroupMember> Result = new ArrayList<>();
        SqlHelper helper = new SqlHelper(context);
        User user = helper.getUser(); //Get yourself to add to group members list

        for(ParcelContacts c:Contacts){
            GroupMember contact = new GroupMember();
            contact.Contact_Id = c.Contact_ID;
            contact.Join_date = c.Join_Date;

            Result.add(contact);
        }
        GroupMember myself = new GroupMember();
        myself.Contact_Id = user.ID;
        myself.Join_date = new Date().toString();

        Result.add(myself);

        return Result;
    }
}
