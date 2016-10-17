package yogispark.chat.Utility;

/**
 * Created by yogesh on 5/10/16.
 */
public class Constants {

    public static final String NEW_MESSAGE_FILTER = "yogispark.chat.new_message";
    public static final String SEND_MESSAGE_FILTER = "yogispark.chat.send_message";
    public static final String USER_REGISTERED_FILTER = "yogispark.chat.registered";
    public static final String CONTACTS_FETCH = "yogispark.chat.contacts_fetched";
    public static final String GROUP_FILTER = "yogispark.chat.group";
    public static final String GROUP_CREATED_FILTER = "yogispark.chat.group_created";
    public static final String GROUP_EXIT_FILTER = "yogispark.chat.group_exit";

    public static final String SOCKET_URL = "http://192.168.43.15:3000";
    public static final String REGISTER_URL = "http://192.168.43.15:3000/api/register";
    public static final String FETCH_CONTACTS_URL = "http://192.168.43.15:3000/api/fetch_contacts";
    public static final String SYNC_CONTACTS = "http://192.168.43.15:3000/api/sync_contacts";

    public static final int SYNC_JOB_ID = 11;

    public static final int CATEGORY_PRIVATE_MESSAGE = 1;
    public static final int CATEGORY_GROUP_MESSAGE = 2;
    public static final int CATEGORY_SYSTEM_MESSAGE = 3;

    public static final int NEW_MESSAGE = 1;
    public static final int MESSAGE_POSTED = 2;
    public static final int MESSAGE_DELIVERED = 3;
    public static final int MESSAGE_READ = 4;

    public static final int GROUP_MEMBERS_ADDED = 5;
    public static final int GROUP_MEMBERS_REMOVED = 6;
    public static final int GROUP_CREATE = 7;
    public static final int GROUP_EXIT = 8;
    public static final int GROUP_EXIT_SUCCESSFULL = 9;
    public static final int GROUP_EXIT_ERROR = 10;

    public static final int MESSAGE_SEND = 10;
    public static final int MESSAGE_RECEIVE = 20;

    public static int CONTACT_SELECT_FOR_NEW_GROUP = 30;
    public static int CONTACT_SELECT_FOR_ADD_TO_GROUP = 40;

    public static final String CONTACT_TYPE_PRIVATE = "private";
    public static final String CONTACT_TYPE_GROUP = "group";







}
