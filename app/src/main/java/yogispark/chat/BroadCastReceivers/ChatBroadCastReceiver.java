package yogispark.chat.BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;

import java.util.ArrayList;

import yogispark.chat.Fragments.ChatViewFragment;
import yogispark.chat.Models.Message;
import yogispark.chat.UI.ChatView;

/**
 * Created by yogesh on 5/10/16.
 */
public class ChatBroadCastReceiver extends BroadcastReceiver {

    ArrayList<Message> Messages;

    public ChatBroadCastReceiver(ArrayList<Message> Messages){
        this.Messages = Messages;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ChatView.CONTACT_ID)){

        }
    }
}
