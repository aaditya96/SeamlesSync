package com.example.exponentcoders.seamlessync;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {
    final public static String ONE_TIME = "onetime";
    CountDownTimer x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        set_folder_moniter_status();
        set_path();


        SharedPreferences pref = getSharedPreferences("SeamlesSync_ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, add_device.class);
            startActivity(intent);
         //   finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }


    }

    public void change_settings(View view)
    {
        startActivity(new Intent(getApplicationContext(),add_device.class));
    }

public void show_log(View v)
{

    Log_Keeper log_keeper=new Log_Keeper(getApplicationContext());
    log_keeper.Execute_Log();
}
    public void start_service(View view)
    {
        /*CHECK IF PHONE IS CONNECTED
        *
        * */


            SharedPreferences moniter_status = getSharedPreferences("moniterstatus", Context.MODE_PRIVATE);
            int current_status = moniter_status.getInt("moniter_folder", -1);
            if (current_status == 0 || current_status == -1) {
                SharedPreferences.Editor editor = moniter_status.edit();
                editor.putInt("moniter_folder", 1);
                editor.commit();
                Log.v("erre=====", "erwerwerwe");
                startService(new Intent(getApplicationContext(), FileObserverService.class));
                set_folder_moniter_status();

            } else if (current_status == 1) {
                Toast.makeText(getApplicationContext(), "Moniter Mode Already ON", Toast.LENGTH_SHORT).show();
            }

    }


    public void stop_service(View view)
    {  SharedPreferences moniter_status=getSharedPreferences("moniterstatus", Context.MODE_PRIVATE);
        int current_status=moniter_status.getInt("moniter_folder",-1);
        if(current_status==1||current_status==-1) {
            SharedPreferences.Editor editor=moniter_status.edit();
            editor.putInt("moniter_folder",0);
            editor.commit();
            set_folder_moniter_status();
           stopService(new Intent(getApplicationContext(),FileObserverService.class));
        }
        else if(current_status==0)
        {

            Toast.makeText(getApplicationContext(),"Moniter Mode Already OFF",Toast.LENGTH_SHORT).show();

        }

    }


    public void refresh(View v)
    {
       startActivity(new Intent(getApplicationContext(),Splash_Screen.class));
    }
//=======================================================
    public void  set_folder_moniter_status()
    {

        SharedPreferences moniter_status=getSharedPreferences("moniterstatus", Context.MODE_PRIVATE);
        int file_observer_status=  moniter_status.getInt("moniter_folder",-1);
        TextView current_status_text_view=(TextView)findViewById(R.id.Current_Status);

        if(file_observer_status==1) {
            current_status_text_view.setText("Monitering Folder");
        }
        else if(file_observer_status==-1)
        {
            current_status_text_view.setText("Error_");
        }
        else if(file_observer_status==0)
        {
            current_status_text_view.setText("Not Monitering Folder");
        }

    }

    public void set_path()
    {
        TextView path=(TextView)findViewById(R.id.folder_path);
        SharedPreferences device_setup;
        device_setup=getSharedPreferences("CONNECTION_INFO", Context.MODE_PRIVATE);
        String FILE1=device_setup.getString("PATH","Unkonown");
        path.setText(FILE1);
        //hello
    }




}