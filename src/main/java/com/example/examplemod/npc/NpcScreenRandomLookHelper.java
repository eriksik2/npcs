package com.example.examplemod.npc;

public class NpcScreenRandomLookHelper {
    
    private int scaleX;
    private int scaleY;

    private float oldX;
    private float oldY;
    private float targetX;
    private float targetY;
    private float x;
    private float y;
    private float t;

    private float randomDelay = 0;

    public NpcScreenRandomLookHelper(int scaleX, int scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        oldX = 0;
        oldY = 0;
        targetX = 0;
        targetY = 0;
        t = 1;
    }

    public NpcScreenRandomLookHelper() {
        this(100, 10);
    }

    public void tick() {
        if(t >= 1) {
            randomDelay -= 0.05;
            if(randomDelay <= 0) {
                float delayCurve = (float) (1-Math.pow(1-randomDelay, 2));
                randomDelay = (float) (delayCurve * 10 + 0.5);
                float randomX = (float) (Math.random()*2 - 1) * scaleX;
                float randomY = (float) (Math.random()*2 - 1) * scaleY;
                setTarget(randomX, randomY);
            }
        }

        float curvedT = (float) (1-Math.pow(1-t, 2));
        x = oldX + (targetX - oldX) * curvedT;
        y = oldY + (targetY - oldY) * curvedT;

        t += 0.005;
        if(t >= 1) {
            t = 1;
        }
    }

    public void setTarget(float x, float y) {
        oldX = this.x;
        oldY = this.y;
        targetX = x;
        targetY = y;
        t = 0;;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
