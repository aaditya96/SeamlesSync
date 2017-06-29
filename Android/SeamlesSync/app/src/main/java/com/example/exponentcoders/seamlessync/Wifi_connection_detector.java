package com.example.exponentcoders.seamlessync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.FileObserver;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ash on 16/9/16.
 */
public class Wifi_connection_detector extends BroadcastReceiver {
    private FileObserverService fileObserverService;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI)
        {
            Toast.makeText(context, "Wifi Found", Toast.LENGTH_SHORT).show();
            SharedPreferences moniter_status=context.getSharedPreferences("moniterstatus", Context.MODE_PRIVATE);
            int moniter_folder   =  moniter_status.getInt("moniter_folder",-1);
            if (moniter_folder== 1)
            {
                Toast.makeText(context, "Moniter Mood ON", Toast.LENGTH_SHORT).show();

                //context.startService(new Intent(context, FileObserverService.class));
                context.startService(new Intent(context, NetworkMonitor.class));
            }

            else if(moniter_folder==0)
            {
                Toast.makeText(context, "Moniter Mood off", Toast.LENGTH_SHORT).show();

            }
            else
            {

                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();

            }

        }

        else {
            //  Log.d("WifiReceiver", "Don't have Wifi Connection");
            Toast.makeText(context,"Network Disconnected",Toast.LENGTH_SHORT).show();

            //context.stopService(new Intent(context,FileObserverService.class));
            context.stopService(new Intent(context, NetworkMonitor.class));

        }
    }
}
