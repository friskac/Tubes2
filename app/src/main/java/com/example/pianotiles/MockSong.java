package com.example.pianotiles;

import java.util.ArrayList;

public class MockSong {

    public static Song getMockSong(){
        Song example = new Song("Mock Song", 120000, 0);
        example.addNote(new Note( 0, 1000, 2000));
        example.addNote(new Note( 0, 5000, 10000));
        example.addNote(new Note( 2, 9000, 8000));
        example.addNote(new Note( 1, 11000, 9000));
        example.addNote(new Note( 3, 11000, 3000));

        return example;
    }
}
