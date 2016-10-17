package yogispark.chat.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yogispark.chat.Adapters.PagerAdapter;
import yogispark.chat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContainerFragment extends Fragment implements TabLayout.OnTabSelectedListener{

    ViewPager viewPager;
    TabLayout tabLayout;
    public ContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_container, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.tab_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        setTabLayout(tabLayout);
        setUpViewPager(viewPager);
        return view;
    }

    private void setUpViewPager(ViewPager viewPager) {
        PagerAdapter pagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager());
        pagerAdapter.addFragments(new GlobalChatFragment(),"Global");
        pagerAdapter.addFragments(new MessageViewFragment(),"Chat");
        pagerAdapter.addFragments(new ContactsFragment(),"Contacts");
        viewPager.setAdapter(pagerAdapter);
    }

    private void setTabLayout(TabLayout tabLayout){
        tabLayout.addTab(tabLayout.newTab().setText("Global"));
        tabLayout.addTab(tabLayout.newTab().setText("Chat"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.addOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
