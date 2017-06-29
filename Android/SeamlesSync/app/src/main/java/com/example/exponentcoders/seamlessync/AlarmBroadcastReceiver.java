package com.example.exponentcoders.seamlessync;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    Context ctx;
    final public static String ONE_TIME = "onetime";
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx=context;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        DirectoryRecordKeeper directoryRecordKeeper=new DirectoryRecordKeeper(context);

        /*  directoryRecordKeeper.Get_Current_Directory_List();
            directoryRecordKeeper.Get_Old_Directory_List();
            directoryRecordKeeper.Get_Directories_Added();
            directoryRecordKeeper.Update();
            directoryRecordKeeper.Get_Directories_Removed();
            directoryRecordKeeper.Save_Directory_List(__);
        */

        ArrayList<String> added_directory=directoryRecordKeeper.Get_Directories_Added();
     //   ArrayList<String>removed_directory=directoryRecordKeeper.Get_Directories_Removed();
        Log_Keeper log_keeper=new Log_Keeper(ctx);
        log_keeper.Add_Send_Directory_Log(added_directory);
   //     log_keeper.Add_Delete_Directory_Log(removed_directory);
      //  Log.v("Added","=====");
      //  directoryRecordKeeper.print_Rec(added_directory);
      //  Log.v("Removed","=====");
     //  directoryRecordKeeper.print_Rec(directoryRecordKeeper.Get_Directories_Removed());
       directoryRecordKeeper.Update();
        //Release the lock
        wl.release();
        //Log.v("Alaram","efefed");
    }
    public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 1, pi);
    }

    public void SetOnce(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
       // am.set
       // am.set(AlarmManager., System.currentTimeMillis(), 1000 * 1, pi);
    }
    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Log.v("Alaram======","Cancelled");
        Toast.makeText(context,"Alaram Cancelled",Toast.LENGTH_SHORT);
    }

}