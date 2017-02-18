package yogispark.chat.Utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.util.Pools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

                NotificationCompat.Builder notificationCompat = new  NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_menu_send)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(body)
                        .setBigContentTitle("New messages")
                        .setSummaryText("You have new messages"))
                        .setGroup("messages")
                        .setAutoCancel(true);

                int uid = (int) System.currentTimeMillis();
                NotificationManagerCompat.from(context).notify(uid, notificationCompat.build());
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

    public static String parseISODate(String date){
        String result= null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        try{
            result = dateFormat.parse(date).toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static Date getGMTDate(String date){
        Date result = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy",Locale.ENGLISH);
        try {
            result = simpleDateFormat.parse(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static String getDatePart(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }

    public static String geyDateTimePart(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd hh:mm a", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }
}
