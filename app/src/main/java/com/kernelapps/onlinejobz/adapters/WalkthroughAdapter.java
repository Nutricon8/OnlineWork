package com.kernelapps.onlinejobz.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kernelapps.onlinejobz.fragments.WalkthroughFragmentOne;
import com.kernelapps.onlinejobz.fragments.WalkthroughFragmentThree;
import com.kernelapps.onlinejobz.fragments.WalkthroughFragmentTwo;

public class WalkthroughAdapter extends FragmentStateAdapter {
    public WalkthroughAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WalkthroughFragmentOne();
            case 1:
                return new WalkthroughFragmentTwo();
            default:
                return new WalkthroughFragmentThree();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // total pages
    }
}
