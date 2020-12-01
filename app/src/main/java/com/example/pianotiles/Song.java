package com.example.pianotiles;

import java.util.ArrayList;
import java.util.Iterator;

public class Song {
    private long duration;
    private ArrayList<Note> notes;
    private int notesSize;
    private int highestScore;
    private String name;

//    public final static int LEVEL_EASY = 0;
//    public final static int LEVEL_MEDIUM = 1;
//    public final static int LEVEL_HARD = 2;

    public Song(String name, long duration, int highestScore) {
        this.name = name;
        this.duration = duration;
        this.notes = new ArrayList<>();
        this.highestScore = highestScore;
    }

    public Song(String name, long duration) {
        this.name = name;
        this.duration = duration;
        this.notes = new ArrayList<>();
        this.highestScore = 0;
    }

    public void addNote(Note newNote) {
        this.notes.add(newNote);
        this.notesSize = this.notes.size();

        //Update duration every time adding new note
        if (this.notesSize > 0) {
            Iterator<Note> iter = this.notes.iterator();

            while (iter.hasNext()) {
                Note note =  iter.next();
                if (note != null) {
                    this.duration += note.getNoteDuration();
                }
            }
        }
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public int getNotesSize() {
        return notesSize;
    }

    public long getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }
}

class Note {
    private String noteName;
    private int xBarIndex;
    private long startTime;
    private long noteDuration;


    public Note(int barIndex, long startTime, long noteDuration,  String noteName) {
        this.xBarIndex = barIndex;
        this.startTime = startTime;
        this.noteDuration = noteDuration;
        this.noteName = noteName;
    }

    public Note(int barIndex, long startTime, long noteDuration) {
        this.xBarIndex = barIndex;
        this.startTime = startTime;
        this.noteDuration = noteDuration;
    }


    public long getNoteDuration() {
        return noteDuration;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getxBarIndex() {
        return xBarIndex;
    }
}
