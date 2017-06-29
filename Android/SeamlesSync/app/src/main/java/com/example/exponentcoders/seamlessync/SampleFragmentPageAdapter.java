package com.example.exponentcoders.seamlessync;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hp on 2/10/16.
 */
public class SampleFragmentPageAdapter extends FragmentPagerAdapter {
    int numTabs;


    public SampleFragmentPageAdapter(FragmentManager fm,int numTabs)
    {
        super(fm);
        this.numTabs=numTabs;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                Page1Fragment tab1=new Page1Fragment();
                return tab1;
            case 1:
                Page2Fragment tab2=new Page2Fragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return numTabs;
    }


}
