package com.example.exponentcoders.seamlessync;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Akshay Patel on 9/25/2016.
 */

public class Log_Keeper {
    Context ctx;
    //private CountDownTimer countDownTimer_for_log;
    public  Log_Keeper(Context context)
    {
        ctx=context;
/*
countDownTimer_for_log=new CountDownTimer(2000,1000 ){
    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {
        Set_NewchangesBit();
        Log.v("Log_Keeper=","Final Log Maintained");
    }
};*/
    }//end main


    private void setCountDownTimer_for_log()
    {
      /*  if(countDownTimer_for_log!=null)
        {   countDownTimer_for_log.cancel();
            countDownTimer_for_log.start();
        }
        else
            countDownTimer_for_log.start();

*/
    }

    public void Add_Send_Directory_Log(ArrayList<String> added_directory)
    {


        if(added_directory==null)
        {

            Log.v("Log Keeper"," No Directory to Send");
        }
        else if(added_directory.size()!=0)
        { //setCountDownTimer_for_log();
            Set_NewchangesBit();
            Log.v("Log_Keeper=","Add Send Directory Log");
            /*
            * Store all the data to be Sent and Deleted in "SeamlesSync_Log_Recoder"
            * "Number_of_Records" is int to stored number of entries in Log_Recorder
            * */

            SharedPreferences log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder",Context.MODE_PRIVATE);
            SharedPreferences.Editor log_record_Editor=log_record.edit();
            //No data Present
            if(!log_record.contains("Number_of_Records"))
            {
                log_record_Editor.putInt("Number_of_Records",0);
                log_record_Editor.commit();
            }
            int new_entry=log_record.getInt("Number_of_Records",-1);
            ++new_entry;
            for(int i=0;i<added_directory.size();i++) {
                log_record_Editor.putString( added_directory.get(i),"Send_"+String.valueOf(new_entry+i));
                new_entry+=i;
                if(added_directory.get(i)!=null) {
                //    RecursiveFileObserver recursiveFileObserver = new RecursiveFileObserver(added_directory.get(i), RecursiveFileObserver.ALL_EVENTS);
                 //   recursiveFileObserver.startWatching();

                }
            }
            log_record_Editor.putInt("Number_of_Records",new_entry);
            log_record_Editor.commit();
            Log.i("Number of Entries=",String.valueOf(log_record.getInt("Number_of_Records",-1)) );
            ctx.startService(new Intent(ctx.getApplicationContext(),FileObserverService.class));

            Log.v("resfreahs","============");

        }
       // Set_NewchangesBit();
    }

    public void Execute_Log()
    {
        /*Write the LOGIC TO EXECUTE THE SENDING AND DELETING FILES AND DIRECTORIES*/

        SharedPreferences log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder",Context.MODE_PRIVATE);


        Map<String, ?> allEntries = log_record.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
               //Directory to send
                if(entry.getValue().toString().contains("Send_"))
                {
                    Log.v("Send Directory", entry.getKey() + ": " + entry.getValue().toString());

                }
                //Directory to Delete
                else if (entry.getValue().toString().contains("Delete_"))
                {
                    Log.v("Delete Directory", entry.getKey() + ": " + entry.getValue().toString());

                }
                //File to Send
                else if(entry.getValue().toString().contains("SendF_"))
                {
                    Log.v("Send Files", entry.getKey() + ": " + entry.getValue().toString());

                }
                //File to delete
                else if(entry.getValue().toString().contains("DeleteF_")){

                    Log.v("Delete File ", entry.getKey() + ": " + entry.getValue().toString());

                }
        }
    }

    public void Add_Delete_File_Log(String to_delete)
    {
        Set_NewchangesBit();
     //   setCountDownTimer_for_log();
        Log.v("Log_Keeper=","Add Delete File Log");


        SharedPreferences log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder",Context.MODE_PRIVATE);
        SharedPreferences.Editor log_record_Editor=log_record.edit();
        if(!log_record.contains("Number_of_Records"))
        {
            log_record_Editor.putInt("Number_of_Records",0);
            log_record_Editor.commit();
        }
        int new_entry=log_record.getInt("Number_of_Records",-1);
        ++new_entry;
        log_record_Editor.putString(to_delete,"DeleteF_"+String.valueOf(new_entry));

        log_record_Editor.putInt("Number_of_Records",new_entry);
        log_record_Editor.commit();
        Log.i("Number of Entries=",String.valueOf(log_record.getInt("Number_of_Records",-1)) );
       // Set_NewchangesBit();
    }
/*
    public void Add_Delete_Directory_Log(ArrayList<String> to_delete)
    {

        if(to_delete==null)
        {

            Log.v("Log Keeper"," No Directory to Delete");
        }
        else  if(to_delete.size()!=0)
        {    Log.v("TO DELETE Size", String.valueOf(to_delete.size()));
            Log.v("TO DELETE",to_delete.get(0));
            Log.v("Log_Keeper=","Add Delete Directory Log 1");
            Set_NewchangesBit();
           // setCountDownTimer_for_log();

            // Store all the data to be Sent and Deleted in "SeamlesSync_Log_Recoder"
            //"Number_of_Records" is int to stored number of entries in Log_Recorder
            //
            SharedPreferences log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder",Context.MODE_PRIVATE);
            SharedPreferences.Editor log_record_Editor=log_record.edit();
            //No data Present
            if(!log_record.contains("Number_of_Records"))
            {
                log_record_Editor.putInt("Number_of_Records",0);
                log_record_Editor.commit();
            }
            int new_entry=log_record.getInt("Number_of_Records",-1);
            ++new_entry;
            for(int i=0;i<to_delete.size();i++) {
                log_record_Editor.putString(to_delete.get(i),"Delete_"+String.valueOf(new_entry+i));
                new_entry+=i;
            }
            log_record_Editor.putInt("Number_of_Records",new_entry);
            log_record_Editor.commit();
            Log.i("Number of Entries=",String.valueOf(log_record.getInt("Number_of_Records",-1)) );
        }
       // Set_NewchangesBit();
    }
*/
    public void Add_Delete_Directory_Log(String to_delete)
    {        Log.v("Log_Keeper=","Add Delete Directory Log 2");
        Set_NewchangesBit();
       // setCountDownTimer_for_log();
         /*
            * Store all the data to be Sent and Deleted in "SeamlesSync_Log_Recoder"
            * "Number_of_Records" is int to stored number of entries in Log_Recorder
            * */
            SharedPreferences log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder",Context.MODE_PRIVATE);
            SharedPreferences.Editor log_record_Editor=log_record.edit();
            //No data Present
            if(!log_record.contains("Number_of_Records"))
            {
                log_record_Editor.putInt("Number_of_Records",0);
                log_record_Editor.commit();
            }
            int new_entry=log_record.getInt("Number_of_Records",-1);
            ++new_entry;

                log_record_Editor.putString(to_delete,"Delete_"+String.valueOf(new_entry));

            log_record_Editor.putInt("Number_of_Records",new_entry);
            log_record_Editor.commit();
            Log.i("Number of Entries=",String.valueOf(log_record.getInt("Number_of_Records",-1)) );

        //Set_NewchangesBit();

    }


    public void Add_Send_File_Log(String to_add)
    {

        Log.v("Log_Keeper=","Add Send File Log");
        Set_NewchangesBit();
       // setCountDownTimer_for_log();
        SharedPreferences log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder",Context.MODE_PRIVATE);
        SharedPreferences.Editor log_record_Editor=log_record.edit();
        if(!log_record.contains("Number_of_Records"))
        {
            log_record_Editor.putInt("Number_of_Records",0);
            log_record_Editor.commit();
        }
        int new_entry=log_record.getInt("Number_of_Records",-1);
        ++new_entry;
        log_record_Editor.putString(to_add,"SendF_"+new_entry);

        log_record_Editor.putInt("Number_of_Records",new_entry);
        log_record_Editor.commit();
        Log.i("Number of Entries=",String.valueOf(log_record.getInt("Number_of_Records",-1)) );

    }



    public void Clear_Log()
    {
        SharedPreferences log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder",Context.MODE_PRIVATE);
        SharedPreferences.Editor log_record_Editor=log_record.edit();
        log_record_Editor.clear();
        log_record_Editor.commit();
        Reset_NewChangesBit();

    }


    public void Set_NewchangesBit()
    {
        SharedPreferences log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder", Context.MODE_PRIVATE);
        SharedPreferences.Editor log_record_Editor=log_record.edit();
        log_record_Editor.putBoolean("NewChanges", Boolean.TRUE);
        log_record_Editor.commit();
    }
    public void Reset_NewChangesBit()
    {
        SharedPreferences log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder", Context.MODE_PRIVATE);
        SharedPreferences.Editor log_record_Editor=log_record.edit();
        log_record_Editor.putBoolean("NewChanges", Boolean.FALSE);
        log_record_Editor.commit();
    }
}
