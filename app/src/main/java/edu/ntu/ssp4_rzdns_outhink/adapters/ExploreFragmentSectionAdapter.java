package edu.ntu.ssp4_rzdns_outhink.adapters;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;



public class ExploreFragmentSectionAdapter extends FragmentPagerAdapter {


    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private ArrayList<String> fragmentTitle = new ArrayList<>();
    
    public ExploreFragmentSectionAdapter(@NonNull FragmentManager fm, int behaviour) {
        super(fm, behaviour);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    public void addFragment(Fragment fm, String title, Bundle bundle){
        //This will be in order when added within
        fm.setArguments(bundle);
        fragmentArrayList.add(fm);
        fragmentTitle.add(title);
    }
}