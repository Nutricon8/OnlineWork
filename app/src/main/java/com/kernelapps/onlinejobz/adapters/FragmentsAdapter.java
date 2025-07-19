package com.kernelapps.onlinejobz.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.fragments.CategoriesFragment;
import com.kernelapps.onlinejobz.fragments.FavoritesFragment;
import com.kernelapps.onlinejobz.fragments.HomeFragment;

public class FragmentsAdapter extends FragmentStatePagerAdapter {

    private static final int TABS = 3;
    private final Context context;

    public FragmentsAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        context = ctx;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case 1:
                fragment = new CategoriesFragment();
                break;
            case 2:
                fragment = new FavoritesFragment();
                break;
            default:
                fragment = new HomeFragment();
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return context.getString(R.string.fragment_categories);
            case 2:
                return context.getString(R.string.fragment_favorites);
            default:
                return context.getString(R.string.fragment_home);
        }
    }

    @Override
    public int getCount() {
        return TABS;
    }
}