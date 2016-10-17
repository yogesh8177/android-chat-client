package yogispark.chat.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import yogispark.chat.Fragments.MessageViewFragment;

/**
 * Created by yogesh on 5/10/16.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> Fragments;
    ArrayList<String> Title;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        Fragments = new ArrayList<>();
        Title = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return Fragments.get(position);
    }

    @Override
    public int getCount() {
        return Fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Title.get(position);
    }

    public void addFragments(Fragment fragment, String title){
        Fragments.add(fragment);
        Title.add(title);
    }

}
