package com.example.exponentcoders.seamlessync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.text.format.Formatter.formatIpAddress;

public class add_device extends AppCompatActivity {
    SharedPreferences device_setup;
    EditText IPAddress;
    EditText PortNumber;
    EditText FilePath;

 //   ValidNetworkData validNetworkData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        load_previous_device();
        //validNetworkData=new ValidNetworkData(getApplicationContext());

        /*
        * Validator for Launching activity only once in android when installed
        *
        *
        * */
 /*
        SharedPreferences pref = getSharedPreferences("SeamlesSync_ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }
*/


    }

    public void save(View view)
    {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!wifi.isConnected()) {
            // Your code here
            Toast.makeText(getApplicationContext(),"Please connected to WIFI network",Toast.LENGTH_SHORT).show();
        }
        else {
            String IP1 = IPAddress.getText().toString().trim();
            String File1 = FilePath.getText().toString().trim();
            String port_num1 = PortNumber.getText().toString().trim();

            if(IP1==null||File1==null||port_num1==null)
            {
                Log.v("Add device =","Empty data");
                Toast.makeText(getApplicationContext(),"Please enter the Fields",Toast.LENGTH_SHORT).show();

            }

            else {
                SharedPreferences.Editor device_setup_editor;
                device_setup = getSharedPreferences("CONNECTION_INFO", Context.MODE_PRIVATE);
                device_setup_editor = device_setup.edit();

                device_setup_editor.putString("IP", IP1);
                device_setup_editor.putString("PORT", port_num1);
                device_setup_editor.putString("PATH", File1);
                device_setup_editor.commit();

        /*GET GATEWAY of ROUTER
        * GET MAC  of ROUTER
        * GET MAC OF LAPTOP
        * */
                send_ping(IP1);


        /*Register Device On Laptop*/
              boolean x=  Login_First_Time(IP1, "user", "pass", Integer.valueOf(PortNumber.getText().toString()));
                if(x==true)
                {  startService(new Intent(getApplicationContext(), NetworkMonitor.class));
                finish();}
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        }
    }



    public void load(View view)
    {

       load_previous_device();
    }


    public void load_previous_device()
    {
        IPAddress = (EditText) findViewById(R.id.editText_ip);
        PortNumber = (EditText) findViewById(R.id.editText_port);
        FilePath = (EditText) findViewById(R.id.editText_path);

        device_setup=getSharedPreferences("CONNECTION_INFO", Context.MODE_PRIVATE);
        String IP1=   device_setup.getString("IP","Unkonown");
        String port_num=device_setup.getString("PORT","Unkonown");
        String FILE1=device_setup.getString("PATH","Unkonown");

        IPAddress.setText(IP1);
        PortNumber.setText(port_num);
        FilePath.setText(FILE1);


    }
    public void send_ping(final String IP)
    {
        /*
        * GET GATEWAY and MAC  address of the Router
        * Store it in SharedPreferences named "MAC_ROUTER"
        * */

        WifiManager wifiManager=(WifiManager)getSystemService(getApplicationContext().WIFI_SERVICE);
        DhcpInfo d=wifiManager.getDhcpInfo();
        String Gateway=intToIp(d.gateway);
        Log.v("GATE WAY========",Gateway);
        //Toast.makeText(getApplicationContext(),Gateway,Toast.LENGTH_SHORT).show();

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String MAC= wifiInfo.getBSSID();
      //  Toast.makeText(getApplicationContext(),MAC,Toast.LENGTH_SHORT).show();
        Log.v("MAC OF ROUTER========",MAC);

//GET MAC ADDRESS OF
     //   Ay ay=new Ay();
      //  ay.execute(IP);
        //Create Runnable

            Runnable send_ping_in_backgroung =new Runnable() {
                @Override
                public void run() {
                    //Your code goes here
                   // String IP1 = IP;
                  //  Boolean
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getByName(IP);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    boolean gatewayReachable;

                    try {
                        gatewayReachable = inet.isReachable(1000);
                        if (gatewayReachable == false) {
                            Log.v("PC NOT REACHABLE", "=========");
                           }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
        //ENd Runnable
        Thread t=new Thread(send_ping_in_backgroung);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
          //  e.printStackTrace();
        }

        Log.v("PING COMPLETE", "=========");

        String PC_MAC=getMacFromArpCache(IP);
        if(PC_MAC!=null)
        {
            Log.v("MAC of LAPTOP",PC_MAC);
            SharedPreferences sharedPreferences=getSharedPreferences("CONNECTION_INFO",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();

            editor.putString("MAC_ROUTER",MAC);
            editor.putString("GATEWAY",Gateway);
            editor.putString("MAC_PC",PC_MAC);
            editor.putString("FOLDER_PC","/ESDLPROJECT/serverdata");
            editor.commit();
            SharedPreferences Sp=getSharedPreferences("CONNECTION_INFO",Context.MODE_PRIVATE);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Device Unreachable",Toast.LENGTH_SHORT).show();

        }


        Log.v("Reading ARP Complete", "=========");

       // Toast.makeText(getApplicationContext(),PC_MAC,Toast.LENGTH_SHORT).show();

    }
    private String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }

    public  String getMacFromArpCache(final String IP) {
    String MAC1="bb";
        if (IP == null)
            return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && IP.equals(splitted[0])) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        MAC1=mac;
                        return mac;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //  return null;
      return  MAC1;
    }


    public boolean Login_First_Time(final String host, final String username,
                                 final String password, final int port)
    {

        /*LOGIN */
        Log.v("Add Device Activity=","Trying to Login ");

        /*
        * Use timer to Check timeout
        * Check after 5000 miliseconds if Login Thread is running
        * If running then Notify no response from Laptop
        *
        * */




        final Boolean[] status = new Boolean[1];
        status[0]=false;
        final MyFTPClient myFTPClient=new MyFTPClient();
        //myFTPClient.mFTPClient.setDefaultTimeout(5000);
        Runnable login_in_backgroung =new Runnable() {
            @Override
            public void run() {

              status[0] =  myFTPClient.ftpConnect(host,username,password,port);
            }

        };

        /*
        final Thread Login_background=new Thread(login_in_backgroung);
        Login_background.start();
        try {
            Login_background.join();
        } catch (InterruptedException e) {}
        */

        /*
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(login_in_backgroung);

        Log.i("Connection", "Waiting for executor to terminate...");
        executor.shutdownNow();
        try {
            executor.awaitTermination(5 * 1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }

        */
        final Thread Login_background=new Thread(login_in_backgroung);
        Login_background.start();
        try {
            Login_background.join(5000);
        } catch (InterruptedException e) {}
        Log.d("AddDevice","After first thread"+status[0]);

        Runnable reallogin =new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(10000, 0);
                }
                catch (InterruptedException e){}
                status[0] =  myFTPClient.ftpConnect(host,username,password,port);
            }

        };
        Thread real = new Thread(reallogin);
        real.start();
        try {
            real.join(15000);
        } catch (InterruptedException e) {}
        Log.d("AddDevice","After second thread"+status[0]);

        Log.v("Add Device Activity="," Login try completed ");




        if(status[0]==false)
        {
            Toast.makeText(getApplicationContext(),"Configuration Falied",Toast.LENGTH_SHORT).show();
            Log.v("Add Device Activity="," Login failed ");

        }
        else
        {
            Toast.makeText(getApplicationContext(),"Configuration Done",Toast.LENGTH_SHORT).show();
            Log.v("Add Device Activity="," Login successfull ");

        }


      //  Login_Background login_background=new Login_Background();
      //  String st[]=new String[]{host,username,password,String.valueOf(port)};

      //  login_background.execute(st);
    return status[0];
    }







}//END CLASS


/*
class  Ay extends AsyncTask<String,Void,Void>
{


    @Override
    protected Void doInBackground(String... params) {
        //Your code goes here
        String IP = params[0];
        InetAddress inet = null;
        try {
            inet = InetAddress.getByName(IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        boolean gatewayReachable;

        try {
            gatewayReachable = inet.isReachable(1000);
            if (gatewayReachable == false) {
                Log.v("PC NOT REACHABLE", "=========");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    return null;

    }

  //  Toast.makeText(getApplicationContext(),MAC_PC,Toast.LENGTH_SHORT).show();
}
*/
 /*
class  Login_Background extends AsyncTask<String,Void,Void>
        {


            @Override
            protected Void doInBackground(String... param) {


                String host= param[0];
                int port=Integer.parseInt(param[3]);
                String user= param[1];
                String pass= param[2];

                  Log.v("HOST",host);
                Log.v("USER",user);
                Log.v("PASS",pass);
                Log.v("PORT",String.valueOf(port));


            //    String host="192.168.0.106";
              //  String user="user";
               // String pass="pass";

                MyFTPClient myFTPClient=new MyFTPClient();
                myFTPClient.ftpConnect(host,user,pass,port);

                return null;
            }
        } */