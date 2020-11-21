package com.example.pianotiles;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
    protected SharedPreferences sharedPreferences;
    protected final static String NAMA_SHARED_PREF = "sp_nilai_display";

    public Storage (Context context){
        this.sharedPreferences = context.getSharedPreferences(NAMA_SHARED_PREF,0);
    }

//    public void saveAll(list<Score> score){
//
//    }
}
