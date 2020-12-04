package com.example.pianotiles;


import android.content.Context;
import android.content.SharedPreferences;
import java.util.Arrays;
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

    /**
     * Method untuk meload dummy highscore - debugging purpose only
     */
    public void loadDummyHighScore() {
        boolean isLoadedDummyScore = this.sharedPref.getBoolean(DUMMY_SCORE_IS_LOADED, false);
        if (!isLoadedDummyScore) {
            SharedPreferences.Editor editor = this.sharedPref.edit();
            Set<String> valueSet = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                valueSet.add((i * 100) + "");
            }
            editor.putStringSet(KEY_HIGH_SCORE, valueSet);
            editor.putBoolean(DUMMY_SCORE_IS_LOADED, true);
            editor.commit();
        }
    }

    /**
     * Method untuk mengambil list nilai highscore dari shared preference
     * @return
     */
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

        Arrays.sort(highScores);

        return highScores;
    }

    /**
     * Memasukkan nilai highscore baru dengan tetap memperhatikan urutan
     * @param newHighScore
     */
    public void insertNewHighScore(int newHighScore){
        Set<String> set = this.sharedPref.getStringSet(KEY_HIGH_SCORE, new HashSet<>());
        int [] highScores;
        if( !set.isEmpty()){
            //Mengambil list highscore dari shared preference
            highScores = new int [set.size()+1];
            Iterator<String> setIterator = set.iterator();
            int counter = 0;

            //memasukkan set nilai dari shared preference ke dalam array int
            while(setIterator.hasNext()){
                String str = setIterator.next();
                //Parse nilai bentuk String ke int
                highScores[counter] = Integer.parseInt(str);
                counter++;
            }

            //Ambil index yang berisi nilai 0 pada list highscore
            int idxValZero = Arrays.binarySearch(highScores, 0);

            //Isi nilai baru di index yang berisi nilai 0
            highScores[idxValZero] = newHighScore;

            //Sort agar terurut dari kecil ke besar
            Arrays.sort(highScores);
            set = new HashSet<>();

            //Ambil nilai dari index 1 sampai dengan highscore.length-1 (n nilai terbesar dari n+1 nilai)
            for(int i=1; i<highScores.length ; i++){
                set.add(highScores[i]+"");
            }

        }else{
            set = new HashSet<>();
            set.add(newHighScore+"");
        }

        SharedPreferences.Editor editor = this.sharedPref.edit();
        editor.putStringSet(KEY_HIGH_SCORE, set);
    }



}
