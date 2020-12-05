package com.example.pianotiles;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UIThreadHandler extends Handler {
    protected final static int MSG_RENDER_TILE = 0;
    protected final static int MSG_TOOGLE_DIALOG = 1;
    protected final static int MSG_CHECK_VISIBILITY = 2;
    protected final static int MSG_REFILL_LIST = 3;
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
        }else if (m.what == UIThreadHandler.MSG_CHECK_VISIBILITY){
            this.gpf.stopOnHide();
        }else if(m.what == UIThreadHandler.MSG_REFILL_LIST){
            this.gpf.fillTheList();
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

    public void checkFragmentVisibility(){
        Message m = new Message();
        m.what = MSG_CHECK_VISIBILITY;
        this.sendMessage(m);
    }

    public void reFillList(){
        Message m = new Message();
        m.what = MSG_REFILL_LIST;
        this.sendMessage(m);
    }
}
