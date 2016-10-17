package yogispark.chat.Models.Parcelable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yogesh on 15/10/16.
 */
public class ParcelContacts implements Parcelable {

    public String Contact_ID;
    public String Contact_Name;
    public String Status;
    public String Join_Date;

    public ParcelContacts(String contact_ID, String contact_Name, String status, String join_Date){
        Contact_ID = contact_ID;
        Contact_Name = contact_Name;
        Status = status;
        Join_Date = join_Date;
    }

    protected ParcelContacts(Parcel in) {
        Contact_ID = in.readString();
        Contact_Name = in.readString();
        Status = in.readString();
        Join_Date = in.readString();
    }

    public static final Creator<ParcelContacts> CREATOR = new Creator<ParcelContacts>() {
        @Override
        public ParcelContacts createFromParcel(Parcel in) {
            return new ParcelContacts(in);
        }

        @Override
        public ParcelContacts[] newArray(int size) {
            return new ParcelContacts[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Contact_ID);
        dest.writeString(Contact_Name);
        dest.writeString(Status);
        dest.writeString(Join_Date);
    }
}
