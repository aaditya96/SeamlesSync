package com.example.exponentcoders.seamlessync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryCancelEvent;
import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryChosenEvent;
import com.turhanoz.android.reactivedirectorychooser.ui.DirectoryChooserFragment;
import com.turhanoz.android.reactivedirectorychooser.ui.OnDirectoryChooserFragmentInteraction;

import java.io.File;

/**
 * Created by hp on 2/10/16.
 */

public class Page2Fragment extends Fragment  {

    ImageView spath,setting;
    File currentRootDirectory;
    String str;
    ToggleButton start_or_stop_monitering;
    TextView Path;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_page2, container, false);
        start_or_stop_monitering=(ToggleButton)v.findViewById(R.id.toggleButton_start_stop);
        start_or_stop_monitering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start_or_stop_monitering.isChecked())
                {
                    start_monitering();
                }
                else
                {
                    stop_monitering();
                }
            }
        });


        Path=(TextView)v.findViewById(R.id.path);
        spath=(ImageView)v.findViewById(R.id.src);
        spath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str=((MainActivity1)getActivity()).Choosedirectory();

            }
        });

        setting=(ImageView)v.findViewById(R.id.set);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(getActivity(),Runonce.class));
            }
        });

        return  v;
    }

    /*void addDirectoryChooserAsFloatingFragment() {
        DialogFragment directoryChooserFragment = DirectoryChooserFragment.newInstance(currentRootDirectory);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        directoryChooserFragment.show(transaction, "RDC");
    }
    @Override
    public void onEvent(OnDirectoryChosenEvent event) {
        currentRootDirectory = event.getFile();
        str=currentRootDirectory.getAbsolutePath();
        Path.setText("yo");

    }

    @Override
    public void onEvent(OnDirectoryCancelEvent event) {

    }*/

    public  void setTextViewText(String value){
        Path.setText(value);
    }
//==

    public void start_monitering()
    {
        /*CHECK IF PHONE IS CONNECTED
        *
        * */


        SharedPreferences moniter_status = getActivity().getSharedPreferences("moniterstatus", Context.MODE_PRIVATE);
        int current_status = moniter_status.getInt("moniter_folder", -1);
        if (current_status == 0 || current_status == -1) {
            SharedPreferences.Editor editor = moniter_status.edit();
            editor.putInt("moniter_folder", 1);
            editor.commit();
            Log.v("erre=====", "erwerwerwe");
            getActivity().startService(new Intent(getActivity(), FileObserverService.class));

        } else if (current_status == 1) {
            Toast.makeText(getActivity(), "Moniter Mode Already ON", Toast.LENGTH_SHORT).show();
        }

    }
    public void stop_monitering()
    {  SharedPreferences moniter_status=getActivity().getSharedPreferences("moniterstatus", Context.MODE_PRIVATE);
        int current_status=moniter_status.getInt("moniter_folder",-1);
        if(current_status==1||current_status==-1) {
            SharedPreferences.Editor editor=moniter_status.edit();
            editor.putInt("moniter_folder",0);
            editor.commit();
            getActivity().stopService(new Intent(getActivity(),FileObserverService.class));
        }
        else if(current_status==0)
        {

            Toast.makeText(getActivity(),"Moniter Mode Already OFF",Toast.LENGTH_SHORT).show();

        }

    }

    //==
}//end of class
