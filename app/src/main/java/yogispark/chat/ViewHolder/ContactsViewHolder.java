package yogispark.chat.ViewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import yogispark.chat.R;

/**
 * Created by yogesh on 14/10/16.
 */
public class ContactsViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView contactName, contactStatus, joinDate;
    ImageView profileImage;

    public ContactsViewHolder(View itemView) {
        super(itemView);

        cardView = (CardView) itemView.findViewById(R.id.contact_cardview);
        contactName = (TextView) cardView.findViewById(R.id.contact_name);
        contactStatus = (TextView) cardView.findViewById(R.id.contact_status);
        joinDate = (TextView) cardView.findViewById(R.id.join_date);
        profileImage = (ImageView) cardView.findViewById(R.id.profile_image);
    }

    public CardView getCardView() {
        return cardView;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }

    public ImageView getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ImageView profileImage) {
        this.profileImage = profileImage;
    }

    public TextView getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(TextView contactStatus) {
        this.contactStatus = contactStatus;
    }

    public TextView getContactName() {
        return contactName;
    }

    public void setContactName(TextView contactName) {
        this.contactName = contactName;
    }

    public TextView getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(TextView joinDate) {
        this.joinDate = joinDate;
    }
}
