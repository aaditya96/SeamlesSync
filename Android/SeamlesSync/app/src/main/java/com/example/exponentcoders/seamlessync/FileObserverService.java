package com.example.exponentcoders.seamlessync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ash on 16/9/16.
 */
public class FileObserverService extends Service {
    RecursiveFileObserver fileObserver;
AlarmBroadcastReceiver alarmBroadcastReceiver;
   static   final String TAG="FILEOBSERVER";
    SharedPreferences device_setup;

    private String to_observe;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate()
    {
        Toast.makeText(getApplicationContext(),"Created File Observer Service",Toast.LENGTH_SHORT).show();
        device_setup=getSharedPreferences("CONNECTION_INFO", Context.MODE_PRIVATE);
        to_observe=device_setup.getString("PATH","Unkonown");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
       fileObserver=new RecursiveFileObserver(to_observe,FileObserver.ALL_EVENTS,getApplicationContext());
       fileObserver.startWatching();
        alarmBroadcastReceiver=new AlarmBroadcastReceiver();
        if(alarmBroadcastReceiver != null){
            alarmBroadcastReceiver.SetAlarm(getApplicationContext());
        }else{
            Toast.makeText(getApplicationContext(), "Alarm is null", Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
        }

    @Override
    public void onDestroy()
    {
        if(alarmBroadcastReceiver != null){
        alarmBroadcastReceiver.CancelAlarm(getApplicationContext());
    }else{
        Toast.makeText(getApplicationContext(), "Alarm is null", Toast.LENGTH_SHORT).show();
    }
       //this.unregisterReceiver(wifi_connection_detecctor);
        Toast.makeText(getApplicationContext(),"File Observer Service Stopped",Toast.LENGTH_SHORT).show();
//        fileObserver.stopWatching();

    }
}
