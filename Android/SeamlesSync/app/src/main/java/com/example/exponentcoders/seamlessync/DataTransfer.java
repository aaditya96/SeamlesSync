package com.example.exponentcoders.seamlessync;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.File;
import java.util.Map;

public class DataTransfer extends Service {
    SharedPreferences logfile;
    Context ctx;
    String IP,Folder,PCFolder; int Port;
    int NOT_ID;
    NotificationManager notificationManager;
    Notification notification;
    NotificationCompat.Builder notificationBuilder;

    public DataTransfer() {
    }
//
    @Override
    public void onCreate()
    {
        logfile = getSharedPreferences("SeamlesSync_Log_Recoder", Context.MODE_PRIVATE);
        ctx = getApplicationContext();
        SharedPreferences conn = getSharedPreferences("CONNECTION_INFO",Context.MODE_PRIVATE);
        Folder = conn.getString("PATH","");
        PCFolder = conn.getString("FOLDER_PC","");
        NOT_ID = 100;

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//Set notification information:
        notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        notificationBuilder.setContentTitle("Syncing Files")
                .setContentText("In progress")
                .setSmallIcon(R.mipmap.ic_launcher);

//Send the notification:
        notification = notificationBuilder.build();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        Bundle extras = intent.getExtras();
        IP = (String)extras.get("IPAddress");
        Port = (int)extras.get("PortNumber");

        Log.d("DataTransfer",IP+":"+Port);
        Log.d("DataTransfer","started");

        startForeground(NOT_ID, notification);
        //SharedPreferences log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder",Context.MODE_PRIVATE);

        Map<String, ?> allEntries = logfile.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            //Directory to send
            if(entry.getValue().toString().contains("Send_"))
            {   final String pathname=entry.getKey();
                Log.v("Send Directory", entry.getKey() + ": " + entry.getValue().toString());
                notificationBuilder.setContentText("Directory Sending");
                notification = notificationBuilder.build();
                notificationManager.notify(NOT_ID,notification);
                Runnable rn = new Runnable() {
                    @Override
                    public void run() {
                        MyFTPClient m = new MyFTPClient();
                        m.ftpConnect(IP, "user", "pass", Port);
                        String prevdir = m.ftpGetCurrentWorkingDirectory();
                        File f = new File(pathname);
                        if(f.isDirectory())
                        {
                            String temp = pathname;
                            temp = "."+temp.replace(Folder,"");
                            m.ftpMakeDirectory(temp);
                        }

                        m.ftpChangeDirectory(prevdir);
                        m.ftpDisconnect();
                    }
                };
                Thread t = new Thread(rn);
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {}


            }
            //Directory to Delete
            else if (entry.getValue().toString().contains("Delete_"))
            {
                final String pathname=entry.getKey();

                Log.v("Delete Directory", entry.getKey() + ": " + entry.getValue().toString());
                notificationBuilder.setContentText("Directory Deleting");
                notification = notificationBuilder.build();
                notificationManager.notify(NOT_ID, notification);
            Runnable rm=new Runnable() {
                @Override
                public void run() {
                    MyFTPClient m = new MyFTPClient();
                    m.ftpConnect(IP, "user", "pass", Port);
                    String prevdir = m.ftpGetCurrentWorkingDirectory();
                    File f = new File(pathname);
                    //if(f.isDirectory())
                    //{
                        String temp = pathname;
                        temp = "."+temp.replace(Folder,"");
                        m.ftpRemoveDirectory(temp);
                    //}
                    m.ftpChangeDirectory(prevdir);
                    m.ftpDisconnect();
                }
            };
               Thread t=new Thread(rm);
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                  //  e.printStackTrace();
                }
            }
            //File to Send

            else if(entry.getValue().toString().contains("SendF_"))
            {

             final   String pathname=entry.getKey();

                Log.v("Send Files", entry.getKey() + ": " + entry.getValue().toString());

                notificationBuilder.setContentText("File Sending");
                notification = notificationBuilder.build();
                notificationManager.notify(NOT_ID, notification);
                //Notification notification = new Notification(R.drawable.ic_launcher.png, "Notification Send", System.currentTimeMillis());
                //Intent notificationIntent = new Intent(this, ExampleActivity.class);
                //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                //notification.setLatestEventInfo(this, getText(R.string.notification_title),
                  //      getText(R.string.notification_message), pendingIntent);

                Runnable rn =new Runnable() {
                    @Override
                    public void run() {
                        MyFTPClient m = new MyFTPClient();
                        m.ftpConnect(IP, "user", "pass", Port);
                        File f = new File(pathname);

                        String prevdir = m.ftpGetCurrentWorkingDirectory();
                        String temp = "."+pathname.replace(Folder,"");
                        temp = temp.replace((new File(pathname).getName()),"");
                        final long filelength= f.length();

                        Log.e("SendFile","before streamAdapter" );
                        CopyStreamAdapter streamadapter = (new CopyStreamAdapter() {

                            @Override
                            public void bytesTransferred(long totalBytesTransferred, int btsTransferred, long streamSize) {
                                //this method will be called everytime some bytes are transferred

                                int percent = (int) (totalBytesTransferred * 100 / filelength);

                                //publishProgress(percent);
                                // update your progress bar with this percentage
                                notificationBuilder.setProgress(100, percent, false);

//Send the notification:
                                notification = notificationBuilder.build();
                                notificationManager.notify(NOT_ID, notification);
                                Log.e("SendFile", "notification set to " + percent);
                            }
                        });
                        m.mFTPClient.setCopyStreamListener(streamadapter);

                        m.ftpUpload(pathname, (new File(pathname).getName()), temp);
                        //System.out.println(m.ftpGetCurrentWorkingDirectory());
                        //int n = s.nextInt();
                        m.ftpChangeDirectory(prevdir);
                        m.ftpDisconnect();
                        Log.e("SendFile", "End of runnable");
                    }
                };
                Thread t=new Thread(rn);
                t.start();
                Log.e("SendFile", "thread started");
                try {
                    t.join();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                Log.e("SendFile","After join" );
                notificationBuilder.setProgress(0,0,false);
                notification = notificationBuilder.build();
                notificationManager.notify(NOT_ID, notification);
                //stopForeground(true);

                //final long filelength= f.length();

                //CopyStreamAdapter streamadapter = (new CopyStreamAdapter() {

                 //   @Override
                 //   public void bytesTransferred(long totalBytesTransferred, int btsTransferred, long streamSize) {
                        //this method will be called everytime some bytes are transferred

                 //       int percent = (int) (totalBytesTransferred * 100 / filelength);
                //
                //        publishProgress(percent);
                //        // update your progress bar with this percentage
                //    }
                //});

                //m.mFTPClient.setCopyStreamListener(streamadapter);


            }

            //File to delete
            else if(entry.getValue().toString().contains("DeleteF_")){
                final String pathname=entry.getKey();

                Log.v("Delete File ", entry.getKey() + ": " + entry.getValue().toString());

                notificationBuilder.setContentText("File deleting");
                notification = notificationBuilder.build();
                notificationManager.notify(NOT_ID,notification);
                Runnable rn=new Runnable() {
                    @Override
                    public void run() {
                        MyFTPClient m = new MyFTPClient();
                        m.ftpConnect(IP, "user", "pass", Port);
                        File f = new File(pathname);

                        String prevdir = m.ftpGetCurrentWorkingDirectory();
                        //if(f.isFile())
                        //{
                            String temp = pathname;
                            temp = "."+temp.replace(Folder,"");
                            m.ftpRemoveFile(temp);
                        //}
                        //System.out.println(m.ftpGetCurrentWorkingDirectory());
                        //int n = s.nextInt();
                        m.ftpChangeDirectory(prevdir);
                        m.ftpDisconnect();
                    }
                };
                Thread t=new Thread(rn);
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }


            }
            //Clear_Log();
            Log_Keeper lk = new Log_Keeper(getApplicationContext());
            lk.Clear_Log();
        }

        stopForeground(true);
        stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}


