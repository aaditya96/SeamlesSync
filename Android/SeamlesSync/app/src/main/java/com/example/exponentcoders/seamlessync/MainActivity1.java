package com.example.exponentcoders.seamlessync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryCancelEvent;
import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryChosenEvent;
import com.turhanoz.android.reactivedirectorychooser.ui.DirectoryChooserFragment;
import com.turhanoz.android.reactivedirectorychooser.ui.OnDirectoryChooserFragmentInteraction;

import java.io.File;


public class MainActivity1 extends AppCompatActivity implements OnDirectoryChooserFragmentInteraction{

    private int[] images={
            R.drawable.ic_info_outline_black_24dp,
            R.drawable.ic_settings_black_24dp
    };
    private String[] tabTitles={
            "STATUS",
            "SETTING"
    };

    String sPath;
    File currentRootDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(!(pref.getBoolean("activity_executed", false))){

            Intent intent = new Intent(this, Runonce.class);
            startActivity(intent);
            finish();
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();

        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();


        }



        TabLayout tabLayout=(TabLayout)findViewById(R.id.tab_layout);

        for(int i=0;i<2;i++){
            tabLayout.addTab(tabLayout.newTab().setIcon(images[i]).setText(tabTitles[i]));
        }
        //tabLayout.addTab(tabLayout.newTab().setIcon());
        //tabLayout.addTab(tabLayout.newTab().setText("Tab2"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final SampleFragmentPageAdapter adapter=new SampleFragmentPageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        });

    }

    public String Choosedirectory(){
        addDirectoryChooserAsFloatingFragment();
        return sPath;
    }

    void addDirectoryChooserAsFloatingFragment() {
        DialogFragment directoryChooserFragment = DirectoryChooserFragment.newInstance(currentRootDirectory);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        directoryChooserFragment.show(transaction, "RDC");
    }
    @Override
    public void onEvent(OnDirectoryChosenEvent event) {
        currentRootDirectory = event.getFile();
        sPath=currentRootDirectory.getAbsolutePath();
        //Page2Fragment page2Fragment=new Page2Fragment();
       // page2Fragment.setTextViewText(sPath);
       SharedPreferences device_setup;
        SharedPreferences.Editor device_setup_editor;

        device_setup = getSharedPreferences("CONNECTION_INFO", Context.MODE_PRIVATE);
        device_setup_editor = device_setup.edit();
           device_setup_editor.putString("PATH", sPath);
        device_setup_editor.commit();
    }

    @Override
    public void onEvent(OnDirectoryCancelEvent event) {

    }


}
