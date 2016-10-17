package yogispark.chat.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yogispark.chat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GlobalChatFragment extends Fragment {


    public GlobalChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_global_chat, container, false);
    }

}
