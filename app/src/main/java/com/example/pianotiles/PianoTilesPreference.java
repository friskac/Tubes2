package com.example.pianotiles;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PianoTilesPreference {
    protected SharedPreferences sharedPref;

    protected final static String NAMA_SHARED_PREF = "sp_piano_tiles";
    protected String DUMMY_SCORE_IS_LOADED = "DUMMY_SCORE_IS_LOADED";
    protected String KEY_HIGH_SCORE = "HIGH_SCORE";

    public PianoTilesPreference(Context context) {
        this.sharedPref = context.getSharedPreferences(NAMA_SHARED_PREF, 0);
        this.loadDummyHighScore();
    }

    public void loadDummyHighScore() {
        boolean isLoadedDummyScore = this.sharedPref.getBoolean(DUMMY_SCORE_IS_LOADED, false);
        if (!isLoadedDummyScore) {
            SharedPreferences.Editor editor = this.sharedPref.edit();
            Set<String> valueSet = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                valueSet.add((i * 100) + "");
            }
            editor.putStringSet(KEY_HIGH_SCORE, valueSet);
            editor.putBoolean(DUMMY_SCORE_IS_LOADED, true);
            editor.commit();
        }
    }

    public int [] getHighScores(){
        Set<String> set = this.sharedPref.getStringSet(KEY_HIGH_SCORE, new HashSet<>());
        int [] highScores;
        if( !set.isEmpty()){
            highScores = new int [set.size()];

            Iterator<String> setIterator = set.iterator();
            int counter = 0;
            while(setIterator.hasNext()){
                String str = setIterator.next();
                highScores[counter] = Integer.parseInt(str);
                counter++;
            }
        }else{
            highScores = new int[1];
        }

        return highScores;
    }



}
