package com.example.exponentcoders.seamlessync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by hp on 3/10/16.
 */
public class Runonce extends AppCompatActivity {
    SharedPreferences device_setup;
    EditText ipadd, port;
    Button btn;

    FragmentManager fm = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_once);

        /*SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, MainActivity2.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }*/

        ipadd = (EditText)findViewById(R.id.ip);
        port = (EditText)findViewById(R.id.prt);
        btn = (Button)findViewById(R.id.button);
        load_previous_device();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try_to_connect(ipadd.getText().toString(),port.getText().toString());

             /*   SampleSupportDialogFragment fragment
                        = SampleSupportDialogFragment.newInstance(
                        7,
                        4,
                        false,
                        false,
                        true,
                        true
                );
                fragment.show(getSupportFragmentManager(), "blur_sample");*/
            }
        });


    }


//============================
public void try_to_connect(String IP1,String port_num1)
    {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!wifi.isConnected()) {
            // Your code here
            Toast.makeText(getApplicationContext(),"Please connected to WIFI network",Toast.LENGTH_SHORT).show();
        }
        else {
        /*    String IP1 = IPAddress.getText().toString().trim();
            String File1 = FilePath.getText().toString().trim();
            String port_num1 = PortNumber.getText().toString().trim();
*/
            if(IP1==null||port_num1==null)
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

                device_setup_editor.commit();

        /*GET GATEWAY of ROUTER
        * GET MAC  of ROUTER
        * GET MAC OF LAPTOP
        * */
                send_ping(IP1);


        /*Register Device On Laptop*/
                boolean x=  Login_First_Time(IP1, "user", "pass", Integer.valueOf(port_num1));
                if(x==true)
                {
                    SampleSupportDialogFragment fragment
                            = SampleSupportDialogFragment.newInstance(
                            7,
                            4,
                            false,
                            false,
                            true,
                            true
                    );
                    fragment.show(getSupportFragmentManager(), "blur_sample");
                    //startService(new Intent(getApplicationContext(), NetworkMonitor.class));
                   // finish();
                }
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        }
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


public  String getMacFromArpCache(final String IP)
{
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


private String intToIp(int addr) {
    return  ((addr & 0xFF) + "." +
            ((addr >>>= 8) & 0xFF) + "." +
            ((addr >>>= 8) & 0xFF) + "." +
            ((addr >>>= 8) & 0xFF));
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
            boolean pcgatewayReachable;

            try {
                pcgatewayReachable = inet.isReachable(1000);
                if (pcgatewayReachable == false) {
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
        SharedPreferences sharedPreferences=getSharedPreferences("CONNECTION_INFO", Context.MODE_PRIVATE);
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



    public void load_previous_device()
    {//   FilePath = (EditText) findViewById(R.id.editText_path);

        device_setup=getSharedPreferences("CONNECTION_INFO", Context.MODE_PRIVATE);
        String IP1=   device_setup.getString("IP","Unkonown");
        String port_num=device_setup.getString("PORT","Unkonown");


        ipadd.setText(IP1);
        port.setText(port_num);
   //     FilePath.setText(FILE1);


    }
//============================

}//end of class
