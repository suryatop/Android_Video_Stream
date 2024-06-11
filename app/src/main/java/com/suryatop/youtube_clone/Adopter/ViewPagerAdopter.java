package com.suryatop.youtube_clone.Adopter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.suryatop.youtube_clone.Dashboard.AboutDashboard;
import com.suryatop.youtube_clone.Dashboard.HomeDashboard;
import com.suryatop.youtube_clone.Dashboard.PlaylistDashboard;
import com.suryatop.youtube_clone.Dashboard.SubscriptionDashboard;
import com.suryatop.youtube_clone.Dashboard.VideoDashboard;

import java.util.ArrayList;

public class ViewPagerAdopter extends FragmentPagerAdapter {

    ArrayList<Fragment>fragments = new ArrayList<>();
    ArrayList<String>Strings = new ArrayList<>();


    public ViewPagerAdopter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeDashboard();

            case 1:
                return new VideoDashboard();

            case 2:
                return new SubscriptionDashboard();

            case 3:
                return new PlaylistDashboard();

            case 4:
                return new AboutDashboard();

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    public void add(Fragment fr, String sr){
        fragments.add(fr);
        Strings.add(sr);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return Strings.get(position);
    }
}
