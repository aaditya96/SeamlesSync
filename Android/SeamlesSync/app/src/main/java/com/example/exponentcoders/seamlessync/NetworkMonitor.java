package com.example.exponentcoders.seamlessync;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NetworkMonitor extends Service {

    private Thread runningThread;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate()
    {

    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        WorkerThread wthread= new WorkerThread(getApplicationContext());
        Thread t = new Thread(wthread);
        t.start();
        //runningThread = performOnBackgroundThread(rn);
        //doScan();
        Log.d("networkthread","started");
        return START_STICKY;
    }
    public void onDestroy()
    {
        Log.d("networkthread", "stopped");
        //runningThread.running = false;
        //runningThread
        WorkerThread.running = false;
        Log.d("networkbackgroundthread","stopped");
    }
/*
    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    } */
/*
    public void doScan() {

        Log.i(LOG_TAG, "Start scanning");

        ExecutorService executor = Executors.newFixedThreadPool(NB_THREADS);
        for(int dest=0; dest<255; dest++) {
            String host = "192.168.0." + dest;
            executor.execute(pingRunnable(host));
        }

        Log.i(LOG_TAG, "Waiting for executor to terminate...");
        executor.shutdown();
        try { executor.awaitTermination(60*1000, TimeUnit.MILLISECONDS); } catch (InterruptedException ignored) { }

        Log.i(LOG_TAG, "Scan finished");
        //Pattern pt = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
        for(int i=0;i<reachableHosts.size();i++)
            try {
                String[] script = {"/system/bin/sh", "-c", "cat /proc/net/arp | grep '" + reachableHosts.get(i).trim() + " '"};
                //"cat /proc/net/arp | grep "+reachableHosts.get(i).trim()
                Process p = Runtime.getRuntime().exec(script);
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = in.readLine();
                if (line != null) {
                    line = line.replaceAll("( )+"," ");
                    String[] splitline = line.split(" ");
                    Log.d("ScanResult", reachableHosts.get(i) + ":- " + splitline[3]);
                }

            } catch (IOException e) {
                Log.e("scanerror", reachableHosts.get(i));
                e.printStackTrace();
            }
    }

    private Runnable pingRunnable(final String host) {
        return new Runnable() {
            public void run() {
                //Log.d(LOG_TAG, "Pinging " + host + "...");
                try {
                    InetAddress inet = InetAddress.getByName(host);
                    boolean reachable = inet.isReachable(1000);
                    if(reachable)
                    {
                        reachableHosts.add(host);
                    }
                } catch (UnknownHostException e) {
                    Log.e(LOG_TAG, "Not found", e);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "IO Error", e);
                }
            }
        };
    }*/
}

class WorkerThread implements Runnable
{
    private static final int NB_THREADS = 10;
    public static boolean running;
    Context ctx;
    private String LOG_TAG;
    private ArrayList<String> reachableHosts;

    WorkerThread(Context ct)
    {
        WorkerThread.running = true;
        ctx = ct;
        LOG_TAG = "scanHosts";
        reachableHosts = new ArrayList<String>();
    }
    public void run()
    {
        //wifi router checking
        SharedPreferences Sp = ctx.getSharedPreferences("CONNECTION_INFO", Context.MODE_PRIVATE);
        String Gateway = Sp.getString("GATEWAY", "");
        Gateway= Gateway+" ";
        Log.d("networkmonitor", "in run method");
        try {
            InetAddress inet = InetAddress.getByName(Gateway);
            boolean gatewayReachable = inet.isReachable(1000);
            Log.d("routercheck","in first try");
            if (gatewayReachable) {
                Log.d("routercheck","reachable");
                String MACROUTER = Sp.getString("MAC_ROUTER","");
                gatewayReachable = inet.isReachable(1000);
                String[] script = {"/system/bin/sh", "-c", "cat /proc/net/arp"};// | grep '" + Gateway.trim() + " '"};
                Process p = Runtime.getRuntime().exec(script);
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while((line=in.readLine())!=null)
                {
                    if(line.contains(Gateway.trim()))
                        break;
                    line = null;
                }
                //line=in.readLine();
                Log.d("routercheck","before if "+line);
                if (line != null) {
                    line = line.replaceAll("( )+"," ");
                    String[] splitline = line.split(" ");
                    Log.d("routercheck",splitline[3].trim());
                    if(!MACROUTER.trim().equals(splitline[3].trim()))
                        return;
                }
                else
                    return;
            }
            else
                return;
        }
        catch (UnknownHostException e){}
        catch (IOException a){}


        while(WorkerThread.running)
        {
            SharedPreferences sp = ctx.getSharedPreferences("SeamlesSync_Log_Recoder", Context.MODE_PRIVATE);
            boolean ToSend = sp.getBoolean("NewChanges",false);
            if(ToSend)
            {
                Log.d("networkthread","sending part");
                String IP= Get_PC_IP();
                Log.d("networkthread","after getpcip");
                if(IP!=null)
                {
                    Intent intnt = new Intent(ctx, DataTransfer.class);
                    intnt.putExtra("IPAddress",IP);
                    intnt.putExtra("PortNumber",Integer.parseInt(Sp.getString("PORT","5555")));

                    ctx.startService(intnt);
                }
            }
            try {Thread.sleep(1000);} catch (InterruptedException e){}
        }
    }

    public String Get_PC_IP() {

        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.i(LOG_TAG, "Start scanning");

            SharedPreferences Sp1 = ctx.getSharedPreferences("CONNECTION_INFO", Context.MODE_PRIVATE);
            String PC_IP = Sp1.getString("IP", "");
            String MAC_PC = Sp1.getString("MAC_PC", "");
            try {
                InetAddress inet1 = InetAddress.getByName(PC_IP);
                boolean PCReachable = inet1.isReachable(1000);
                String[] script1 = {"/system/bin/sh", "-c", "cat /proc/net/arp"};// | grep '" + Gateway.trim() + " '"};
                Process p1 = Runtime.getRuntime().exec(script1);
                BufferedReader in1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                String line1;
                while ((line1 = in1.readLine()) != null) {
                    if (line1.contains(PC_IP.trim()))
                        break;
                    line1 = null;
                }
                //line=in.readLine();
                Log.d("PCcheck", "before if " + line1);
                if (line1 != null) {
                    line1 = line1.replaceAll("( )+", " ");
                    String[] splitline = line1.split(" ");
                    Log.d("PCcheck", splitline[3].trim());
                    if (MAC_PC.trim().equals(splitline[3].trim()))
                        return PC_IP;
                }
            } catch (IOException e) {
            }
            SharedPreferences sp=ctx.getSharedPreferences("CONNECTION_INFO",Context.MODE_PRIVATE);
            String gateway=sp.getString("GATEWAY","err");
            if(gateway.equals("err"))
            {
                Log.v("Error in Gateway value"," ==============================");
            }
            String spl_gate[]=gateway.split(".");
            /*
            * RECONSTRUCTED GATEWAY =recons
            * */
            String recons=spl_gate[0]+"."+spl_gate[1]+"."+spl_gate[2]+".";
            Log.v("GATEWAY RETRIVED",recons);
            ExecutorService executor = Executors.newFixedThreadPool(NB_THREADS);
            for (int dest = 0; dest < 255; dest++) {


                String host = recons + dest;
                executor.execute(pingRunnable(host));
            }

            Log.i(LOG_TAG, "Waiting for executor to terminate...");
            executor.shutdown();
            try {
                executor.awaitTermination(60 * 1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {
            }

            Log.i(LOG_TAG, "Scan finished");
            //Pattern pt = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
            for (int i = 0; i < reachableHosts.size(); i++)
                try {
                    String[] script = {"/system/bin/sh", "-c", "cat /proc/net/arp | grep '" + reachableHosts.get(i).trim() + " '"};
                    //"cat /proc/net/arp | grep "+reachableHosts.get(i).trim()
                    Process p = Runtime.getRuntime().exec(script);
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = in.readLine();
                    if (line != null) {
                        line = line.replaceAll("( )+", " ");
                        String[] splitline = line.split(" ");
                        Log.d("ScanResult", reachableHosts.get(i) + ":- " + splitline[3]);
                        if (MAC_PC.trim().equals(splitline[3]))
                            return reachableHosts.get(i);
                    }

                } catch (IOException e) {
                    Log.e("scanerror", reachableHosts.get(i));
                    e.printStackTrace();
                }
            return null;
        }
        return null;
    }

    private Runnable pingRunnable(final String host) {
        return new Runnable() {
            public void run() {
                //Log.d(LOG_TAG, "Pinging " + host + "...");
                try {
                    InetAddress inet = InetAddress.getByName(host);
                    boolean reachable = inet.isReachable(1000);
                    if(reachable)
                    {
                        reachableHosts.add(host);
                    }
                } catch (UnknownHostException e) {
                    Log.e(LOG_TAG, "Not found", e);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "IO Error", e);
                }
            }
        };
    }

}