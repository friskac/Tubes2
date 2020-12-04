package com.example.pianotiles;

public class Presenter {
    public PianoTilesPreference ptf;
    public Presenter(PianoTilesPreference pianoTilesPreference ){
        this.ptf = pianoTilesPreference;
    }

    public PianoTilesPreference getPreference(){
        return this.ptf;
    }
}
