package com.example.pianotiles;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UIThreadHandler extends Handler {
    protected final static int MSG_SET=0;
    protected GamePlayFragment gpf;

    public UIThreadHandler (GamePlayFragment gpf)
    {
        super();
        this.gpf=gpf;
    }

    @Override
    public void handleMessage(@NonNull Message m) {
            super.handleMessage(m);
            if (m.what == UIThreadHandler.MSG_SET) {

                ArrayList<Tiles> tiles;
                if (m.obj instanceof ArrayList) {
                    tiles = (ArrayList<Tiles>) m.obj;

                    for(int i =0; i<tiles.size(); i++){
                        this.gpf.renderTiles2(tiles.get(i));
                    }

                }
            }
        }

    public void setMove(ArrayList<Tiles> tiles){
        Message m=new Message();
        m.what=MSG_SET;
        m.obj=tiles;
        this.sendMessage(m);
    }

    public GamePlayFragment getFragmentGame(){
        return gpf;
    }
}
