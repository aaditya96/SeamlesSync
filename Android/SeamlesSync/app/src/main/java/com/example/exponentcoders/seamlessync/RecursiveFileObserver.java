package com.example.exponentcoders.seamlessync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by ash on 16/9/16.
 */
public class RecursiveFileObserver extends FileObserver {
    static SharedPreferences device_setup;
    Context ctx;
    SharedPreferences log_record;
    SharedPreferences.Editor log_record_Editor;
    List<SingleFileObserver> mObservers;
    String mPath;
    int mMask;

    public RecursiveFileObserver(String path, int mask, Context context) {
        this(path, mask);
        ctx=context;
        log_record=ctx.getSharedPreferences("SeamlesSync_Log_Recoder",Context.MODE_PRIVATE);
        log_record_Editor=log_record.edit();

    }

    public RecursiveFileObserver(String path, int mask) {
        super(path, mask);
        mPath = path;
        mMask = mask;

    }

    @Override
    public void startWatching() {
        if (mObservers != null) return;
        mObservers = new ArrayList<SingleFileObserver>();
        Stack<String> stack = new Stack<String>();
        stack.push(mPath);
        while (!stack.empty()) {
            String parent = stack.pop();
            mObservers.add(new SingleFileObserver(parent, mMask));
            File path = new File(parent);
            File[] files = path.listFiles();
            if (files == null) continue;
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory() && !files[i].getName().equals(".")
                        && !files[i].getName().equals("..")) {
                    stack.push(files[i].getPath());
                }
            }
        }
        for (int i = 0; i < mObservers.size(); i++)
            mObservers.get(i).startWatching();
    }

    @Override
    public void stopWatching() {
        if (mObservers == null) return;

        for (int i = 0; i < mObservers.size(); ++i)
            mObservers.get(i).stopWatching();

        mObservers.clear();
        mObservers = null;
    }

    @Override
    public void onEvent(int event, String path) {

    }

    public class SingleFileObserver extends FileObserver {
        private String mPath;

        public SingleFileObserver(String path, int mask) {
            super(path, mask);
            mPath = path;
        }

        @Override
        public void onEvent(int event, String path) {
            String newPath = mPath + "/" + path;
            String TAG="FILE OBSERVER: ";

            Log_Keeper log_keeper=new Log_Keeper(ctx);

            RecursiveFileObserver.this.onEvent(event, newPath);
            switch(event){

             //   case FileObserver.ACCESS:
             //       Log.d(TAG, "Access:" + newPath);
             //   case FileObserver.OPEN:
             //       Log.d(TAG, "OPEN:" + newPath);
                case FileObserver.CREATE:
                    Log.d(TAG, "CREATE:" + newPath);
                    log_keeper.Add_Send_File_Log(newPath);
                    break;
                case FileObserver.DELETE:
                    Log.d(TAG, "DELETE:" + newPath);
                    log_keeper.Add_Delete_File_Log(newPath);
                    break;

                case FileObserver.DELETE_SELF:
                    Log.d(TAG, "DELETE_SELF:" +newPath);
                    log_keeper.Add_Delete_Directory_Log(newPath.replace("/null",""));
                    break;

                 case FileObserver.MODIFY:
                 Log.d(TAG, "MODIFY:" +newPath);

                case FileObserver.MOVED_FROM:

                  Log.d(TAG, "MOVED_FROM:" + newPath);
                    break;
               case FileObserver.MOVED_TO:
                    Log.d(TAG, "MOVED_TO:" + path);
                    break;
                case FileObserver.MOVE_SELF:
                    Log.d(TAG, "MOVE_SELF:" + path);
                    break;

                default:
                    // just ignore
                    break;
            }


            if( event==FileObserver.MOVE_SELF && event==FileObserver.MOVED_TO) {
                Log.d(TAG, "MOVE_SELF and move to:" + path);
            }


            if( event==FileObserver.MOVED_FROM && event==FileObserver.MODIFY) {
                Log.d(TAG, "MOVE_from and modify:" + path);
            }

        }

    }
}