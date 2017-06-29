package com.example.exponentcoders.seamlessync;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Akshay Patel on 9/24/2016.
 */

public class DirectoryRecordKeeper {
    SharedPreferences device_setup;
    private Context ctx;
    DirectoryRecordKeeper(Context context)
    {
        ctx=context;
    }

    /*USED FOR DEBUGGING PURPOSE*/
    public void print_Rec(ArrayList<String> directory_list)
    {
           if (directory_list==null)
           {
               Log.i("Receieved : ", "NULL");
           }
           else if(directory_list.size()==0)
           {
              Log.i("Contains :", "Nothing");
           }
            else
           {
               for (int i = 0; i < directory_list.size(); i++) {
                   Log.i("Name: ", directory_list.get(i));
               }
           }
        return;
    }

    /*Used for scanning Currently Monitored Directory and return list of all Directories with path*/
    private ArrayList<String> Get_Current_Directory_List()
    {    ArrayList<String> directory_list;

        directory_list=new ArrayList() ;
        SharedPreferences device_setup;
        device_setup=ctx.getSharedPreferences("CONNECTION_INFO", Context.MODE_PRIVATE);
        String FILE1=device_setup.getString("PATH","Unkonown");
        Stack<String> stack = new Stack<String>();
        stack.push(FILE1);
         while (!stack.empty()) {
            String parent = stack.pop();
            directory_list.add(parent);
            File path = new File(parent);
            File[] files = path.listFiles();
            if (files == null) continue;
            for (int i = 0; i < files.length; ++i) {
                //if (!files[i].getName().equals(".")
                   //     && !files[i].getName().equals("..")) {
                if (files[i].isDirectory() && !files[i].getName().equals(".")
                      && !files[i].getName().equals("..")) {
                    stack.push(files[i].getPath());
                }
            }//end for

        }//end while
        return  directory_list;
    }

    /*
    * This function is used for storing the list of latest directories in the SharedPreferences
    * Name of SharedPreference = "seamlessync_DirecoryList"
    * */
    private void Save_Directory_List(ArrayList<String> directory_list_to_save)
    {
        SharedPreferences Seamlesssync_DirectoryList=ctx.getSharedPreferences("seamlessync_DirectoryList",Context.MODE_PRIVATE);
        SharedPreferences.Editor Seamlesssync_DirectoryList_Editor=Seamlesssync_DirectoryList.edit();
        Seamlesssync_DirectoryList_Editor.clear();
        Seamlesssync_DirectoryList_Editor.commit();
        Set<String>to_save=new HashSet<>();

        to_save.addAll(directory_list_to_save);
        Seamlesssync_DirectoryList_Editor.putStringSet("Directory_list", to_save);
        Seamlesssync_DirectoryList_Editor.commit();
    }

    /*Returns Old List of Directories*/
    private ArrayList<String> Get_Old_Directory_List()
    {
        SharedPreferences Seamlesssync_DirectoryList = ctx.getSharedPreferences("seamlessync_DirectoryList", Context.MODE_PRIVATE);
        Set<String> empptyset=new HashSet<>();
        Set<String> Old_Directory_List_set = Seamlesssync_DirectoryList.getStringSet("Directory_list", empptyset);
        ArrayList<String> Old_List = new ArrayList<String>();
        Old_List.addAll(Old_Directory_List_set);
        return Old_List;

    }

    public ArrayList<String> Get_Directories_Removed()
    {
        ArrayList<String> Old_Directory_List=Get_Old_Directory_List();
        ArrayList<String> New_Directory_List=Get_Current_Directory_List();
        Old_Directory_List.removeAll(New_Directory_List);
       // Log.v("List to remove " ,"Directories");
        return Old_Directory_List;
       /* if (Old_Directory_List!=null) {
            return Old_Directory_List;
        }
        else
        {
            ArrayList<String> empty=new ArrayList<>();
            empty.add("_noChange");
            return empty;

        }*/
    }
    public ArrayList<String> Get_Directories_Added()
    {
        ArrayList<String> New_Directory_List=Get_Current_Directory_List();
        ArrayList<String> Old_Directory_List=Get_Old_Directory_List();
        New_Directory_List.removeAll(Old_Directory_List);
      //  Log.v("New list:" ,"new list");
        return New_Directory_List;

      /*  if (New_Directory_List!=null) {
            return New_Directory_List;
        }
        else
        {
            ArrayList<String> empty=new ArrayList<>();
            empty.add("_noChange");
            return empty;
        }*/
    }


    public void Update()
    {
        ArrayList<String> New=Get_Current_Directory_List();
        Save_Directory_List(New);
    }


}
