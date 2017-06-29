package com.example.exponentcoders.seamlessync;

import android.os.AsyncTask;
import android.view.View;

import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.File;

/**
 * Created by ash on 16/9/16.
 */
public class SendFile extends AsyncTask<String,Integer, Void> {
    @Override
    protected Void doInBackground( String... str)
    {
        MyFTPClient m = new MyFTPClient();
        m.ftpConnect(str[0], "user", "pass", Integer.parseInt(str[1]));


        File f = new File(str[2]);
        final long filelength= f.length();
        CopyStreamAdapter streamadapter = (new CopyStreamAdapter() {

            @Override
            public void bytesTransferred(long totalBytesTransferred, int btsTransferred, long streamSize) {
                //this method will be called everytime some bytes are transferred

                int percent = (int) (totalBytesTransferred * 100 / filelength);

                publishProgress(percent);
                // update your progress bar with this percentage
            }
        });

        m.mFTPClient.setCopyStreamListener(streamadapter);
        m.ftpUpload(str[2], (new File(str[2]).getName()), "./received");
        //System.out.println(m.ftpGetCurrentWorkingDirectory());
        //int n = s.nextInt();
        m.ftpDisconnect();
        return null;
    }

    /*@Override
    protected void onPreExecute()
    {
        MyProgressBar.setProgress(0);
        MyProgressBar.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onPostExecute(Void result)
    {
        MyProgressBar.setVisibility(View.GONE);
    }


    @Override
    protected void onProgressUpdate(Integer... value)
    {
        MyProgressBar.setProgress(value[0]);
    }
*/
}
