package yogispark.chat.Dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import yogispark.chat.R;
import yogispark.chat.UI.MemberInfo;
import yogispark.chat.Utility.Constants;

/**
 * Created by yogesh on 17/10/16.
 */
public class GroupMemberInfo extends DialogFragment {

    TextView view_member, remove_member;
    public String GroupId, MemberId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_member_info_dialog, container, false);
        view_member = (TextView) view.findViewById(R.id.view_member);
        remove_member = (TextView) view.findViewById(R.id.remove_member);

        view_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMember();
            }
        });

        remove_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMember();
            }
        });

        return view;
    }

    private void viewMember() {
        Intent view = new Intent(getContext(), MemberInfo.class);
        view.putExtra("contact_id", MemberId);
        startActivity(view);
        dismiss();
    }

    private void removeMember(){
        Intent remove = new Intent();
        remove.setAction(Constants.GROUP_FILTER);

        remove.putExtra("category", Constants.GROUP_MEMBERS_REMOVED);
        remove.putExtra("group_id", GroupId);
        remove.putExtra("contact_id", MemberId);

        getActivity().sendBroadcast(remove);
        dismiss();
    }
}
