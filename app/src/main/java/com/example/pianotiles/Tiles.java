package com.example.pianotiles;

import android.graphics.Color;

public class Tiles {
    protected int index, x, y, width, height, color;
    boolean isPressed, isReleased, isMissed;

    public Tiles(int index, int x, int y, int width, int height) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isPressed = false;
        this.isReleased = false;
        this.isMissed = false;

        //Warna default hitam
        this.color = Color.argb(255, 0, 0, 0);
    }

    public int getIndex() {
        return index;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public void setReleased(boolean released) {
        isReleased = released;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public boolean isReleased() {
        return isReleased;
    }

    public int top() {
        return this.y - this.height;
    }

    public int bottom() {
        return this.y;
    }

    public int left() {
        return this.x;
    }

    public int right() {
        return this.x + this.width;
    }

    @Override
    public String toString() {
        return "Tiles{" +
                "index=" + index +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", color=" + color +
                ", isPressed=" + isPressed +
                ", isReleased=" + isReleased +
                ", isMissed=" + isMissed +
                '}';
    }
}
