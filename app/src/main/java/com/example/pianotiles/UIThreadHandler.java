package com.example.pianotiles;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UIThreadHandler extends Handler {
    protected final static int MSG_RENDER_TILE = 0;
    protected final static int MSG_TOOGLE_DIALOG = 1;
    protected GamePlayFragment gpf;

    public UIThreadHandler(GamePlayFragment gpf) {
        super();
        this.gpf = gpf;
    }

    @Override
    public void handleMessage(@NonNull Message m) {
        super.handleMessage(m);
        if (m.what == UIThreadHandler.MSG_RENDER_TILE) {
            this.gpf.renderTiles();
        }
        else if (m.what == UIThreadHandler.MSG_TOOGLE_DIALOG){
            this.gpf.showGameDialog();
        }
    }

    public void setMove() {
        Message m = new Message();
        m.what = MSG_RENDER_TILE;
        this.sendMessage(m);
    }

    public GamePlayFragment getFragmentGame() {
        return gpf;
    }

    public void toggleDialog() {
        Message m = new Message();
        m.what = MSG_TOOGLE_DIALOG;
        this.sendMessage(m);
    }
}
