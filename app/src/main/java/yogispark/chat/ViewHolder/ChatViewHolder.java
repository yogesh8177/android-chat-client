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
public class ChatViewHolder extends RecyclerView.ViewHolder {
    CardView cardView;
    TextView contactName;
    TextView contactStatus;
    TextView message;
    TextView postedDate;
    TextView dateIndicator;
    ImageView ticks;

    public ChatViewHolder(View itemView) {
        super(itemView);
        cardView = (CardView) itemView.findViewById(R.id.chat_cardView);
        contactName = (TextView) cardView.findViewById(R.id.chat_contact_name);
        contactStatus = (TextView) cardView.findViewById(R.id.chat_contact_status);
        message = (TextView) cardView.findViewById(R.id.chat_message);
        postedDate = (TextView) cardView.findViewById(R.id.chat_post_date);
        dateIndicator = (TextView) cardView.findViewById(R.id.date_indicator);
        ticks = (ImageView) cardView.findViewById(R.id.ticks);
    }

    public CardView getCardView() {
        return cardView;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }

    public TextView getContactName() {
        return contactName;
    }

    public void setContactName(TextView contactName) {
        this.contactName = contactName;
    }

    public TextView getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(TextView contactStatus) {
        this.contactStatus = contactStatus;
    }

    public TextView getMessage() {
        return message;
    }

    public void setMessage(TextView message) {
        this.message = message;
    }

    public TextView getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(TextView postedDate) {
        this.postedDate = postedDate;
    }

    public TextView getDateIndicator() {
        return dateIndicator;
    }

    public void setDateIndicator(TextView dateIndicator) {
        this.dateIndicator = dateIndicator;
    }

    public ImageView getTicks() {
        return ticks;
    }

    public void setTicks(ImageView ticks) {
        this.ticks = ticks;
    }
}
