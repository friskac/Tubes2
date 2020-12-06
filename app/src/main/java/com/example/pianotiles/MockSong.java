package com.example.pianotiles;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class MockSong {

    public static ArrayList<Tiles> generateMockTiles(int canvasWidth, int canvasHeight) {
        Random rand = new Random();
        //Banyaknya jalur untuk tile
        int barIndexLimit = 4;

        ArrayList<Tiles> notes = new ArrayList<>();

        //Jumlah tile yang akan dibuat
        int numberOfTiles = 100;
        int counter = 0;

        //Lebar dari tile
        int noteWidth = canvasWidth / barIndexLimit;

        //Persentase tinggi tile minimal dibandingkan tinggi canvas
        int percentTileHeightToCanvas = 5;
        //Tinggi minimal tile berdasarkan persentase
        int tileMinHeight = canvasHeight / percentTileHeightToCanvas;

        //Tinggi maksimal sebuah tile
        int heightMultiplierLimit = 4;

        //Spasi antar tile
        int tileSpacing = tileMinHeight;


        Tiles prevTile = null;
        int prevTileIndex = 0;
        while (counter < numberOfTiles) {
            Tiles currTile = null;
            int currTileIndex = 0;
            if (prevTile == null) {
                currTileIndex = rand.nextInt(barIndexLimit);
                int currNoteHeight = tileMinHeight * rand.nextInt(heightMultiplierLimit);
                currTile = new Tiles(currTileIndex,noteWidth * currTileIndex, 0, noteWidth, currNoteHeight);
            } else {
                currTileIndex = rand.nextInt(barIndexLimit);

                while (currTileIndex == prevTileIndex) {
                    currTileIndex = rand.nextInt(barIndexLimit);
                }

                int currNoteHeight = tileMinHeight * rand.nextInt(heightMultiplierLimit);
                currTile = new Tiles(currTileIndex,noteWidth * currTileIndex, prevTile.top() - tileSpacing, noteWidth, currNoteHeight);
            }

            notes.add(currTile);

            prevTile = currTile;
            prevTileIndex = currTileIndex;
            counter++;
        }

        return notes;
    }
}
