package com.example.pianotiles;

public class SensorBar {
    final static int CIRCLE_MOVEMENT_SPEED = 15;
    int canvasWidth, canvasHeight;
    //Line coordinates
    int left, bottom, right, top, lineWidth;
    //Circle coordinates
    int circleColor;
    float cx, cy, radius, cxMiniCircle, cyMiniCircle, radMiniCircle;

    public SensorBar(int canvasWidth, int canvasHeight) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;

        this.lineWidth = 5;
        this.left = 0;
        this.right = canvasWidth;
        this.bottom = canvasHeight - (canvasHeight/5);
        this.top = this.bottom-lineWidth;

        this.radius = (canvasHeight/40);
        this.cx = left;
        this.cy = this.bottom-(int)(this.radius/2)+(lineWidth*2);

        this.radMiniCircle = this.radius-(this.radius*10/100);
        this.cxMiniCircle = this.cx;
        this.cyMiniCircle = this.cy;
    }

    //Modifikasi nilai center dari lingkaran pada sensor
    public void modifyCX(float newValue){
        this.cx = this.cx+newValue;
        if(this.cx < 0){
            this.cx = 0;
        }else if(this.cx > canvasWidth){
            this.cx = canvasWidth;
        }
        this.cxMiniCircle = this.cx;
    }

    public float getCircleBottom(){
        return this.cy+this.radius;
    }

    public float getCircleTop(){
        return this.cy-this.radius;
    }

    public float getCircleLeft() {
        return cx-this.radius;
    }

    public float getCircleRight() {
        return cx+this.radius;
    }
}
