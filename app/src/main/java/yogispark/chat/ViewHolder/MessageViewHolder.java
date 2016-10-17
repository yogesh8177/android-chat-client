package yogispark.chat.ViewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import yogispark.chat.R;

/**
 * Created by yogesh on 5/10/16.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    ImageView contactImage;
    TextView contactName;
    TextView postedDate;
    TextView latestMessage;
    TextView count;

    public MessageViewHolder(View itemView) {
        super(itemView);

        cardView = (CardView)itemView.findViewById(R.id.cardView);
        contactImage = (ImageView) itemView.findViewById(R.id.contact_image);
        contactName = (TextView) itemView.findViewById(R.id.contact_name);
        postedDate = (TextView) itemView.findViewById(R.id.posted_date);
        latestMessage = (TextView) itemView.findViewById(R.id.latest_message);
        count = (TextView) itemView.findViewById(R.id.count);
    }

    public CardView getCardView() {
        return cardView;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }

    public ImageView getContactImage() {
        return contactImage;
    }

    public void setContactImage(ImageView contactImage) {
        this.contactImage = contactImage;
    }

    public TextView getContactName() {
        return contactName;
    }

    public void setContactName(TextView contactName) {
        this.contactName = contactName;
    }

    public TextView getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(TextView postedDate) {
        this.postedDate = postedDate;
    }

    public TextView getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(TextView latestMessage) {
        this.latestMessage = latestMessage;
    }

    public TextView getCount() {
        return count;
    }

    public void setCount(TextView count) {
        this.count = count;
    }
}
