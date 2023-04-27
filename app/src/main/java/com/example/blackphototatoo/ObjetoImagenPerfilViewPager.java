package com.example.blackphototatoo;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ObjetoImagenPerfilViewPager extends FragmentPagerAdapter {
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> names = new ArrayList<>();
    private final List<String> times = new ArrayList<>();
    private final List<String> rankings = new ArrayList<>();


    public ObjetoImagenPerfilViewPager(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragment(Fragment fragment, String name, String time, String title) {
        fragments.add(fragment);
        names.add(name);
        times.add(time);
        rankings.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return rankings.get(position);
    }
}
