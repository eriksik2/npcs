package com.example.examplemod.widgets;

public class Color {
    public float red, green, blue, alpha;

    public static Color gray(float value) {
        return new Color(value, value, value);
    }

    public Color(int value) {
        this.red = (float)(value & 0xFF)/255;
        this.green = (float)((value >> 8) & 0xFF)/255;
        this.blue = (float)((value >> 16) & 0xFF)/255;
        this.alpha = (float)((value >> 24) & 0xFF)/255;
    }

    public Color(float r, float g, float b, float a) {
        if(r < 0 || r > 1) throw new IllegalArgumentException("r must be between 0 and 1 (inclusive)");
        if(g < 0 || g > 1) throw new IllegalArgumentException("g must be between 0 and 1 (inclusive)");
        if(b < 0 || b > 1) throw new IllegalArgumentException("b must be between 0 and 1 (inclusive)");
        if(a < 0 || a > 1) throw new IllegalArgumentException("a must be between 0 and 1 (inclusive)");
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1);
    }

    public Color(int r, int g, int b, int a) {
        if(r < 0 || r > 255) throw new IllegalArgumentException("r must be between 0 and 255 (inclusive)");
        if(g < 0 || g > 255) throw new IllegalArgumentException("g must be between 0 and 255 (inclusive)");
        if(b < 0 || b > 255) throw new IllegalArgumentException("b must be between 0 and 255 (inclusive)");
        if(a < 0 || a > 255) throw new IllegalArgumentException("a must be between 0 and 255 (inclusive)");
        this.red = (float)r/255;
        this.green = (float)g/255;
        this.blue = (float)b/255;
        this.alpha = (float)a/255;
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color copy() {
        return new Color(this.red, this.green, this.blue, this.alpha);
    }

    public int getValue() {
        int r = Math.round(this.red*255);
        int g = Math.round(this.green*255);
        int b = Math.round(this.blue*255);
        int a = Math.round(this.alpha*255);
        return (a << 24) | (r << 16) | (b << 8) | g;
    }

    public Color setAlpha(float alpha) {
        if(alpha < 0 || alpha > 1) throw new IllegalArgumentException("alpha must be between 0 and 1 (inclusive)");
        this.alpha = alpha;
        return this;
    }

    public Color setAlpha(int alpha) {
        if(alpha < 0 || alpha > 255) throw new IllegalArgumentException("alpha must be between 0 and 255 (inclusive)");
        this.alpha = (float)alpha/255;
        return this;
    }

    public Color setRed(float red) {
        if(red < 0 || red > 1) throw new IllegalArgumentException("red must be between 0 and 1 (inclusive)");
        this.red = red;
        return this;
    }

    public Color setRed(int red) {
        if(red < 0 || red > 255) throw new IllegalArgumentException("red must be between 0 and 255 (inclusive)");
        this.red = (float)red/255;
        return this;
    }

    public Color setGreen(float green) {
        if(green < 0 || green > 1) throw new IllegalArgumentException("green must be between 0 and 1 (inclusive)");
        this.green = green;
        return this;
    }

    public Color setGreen(int green) {
        if(green < 0 || green > 255) throw new IllegalArgumentException("green must be between 0 and 255 (inclusive)");
        this.green = (float)green/255;
        return this;
    }

    public Color setBlue(float blue) {
        if(blue < 0 || blue > 1) throw new IllegalArgumentException("blue must be between 0 and 1 (inclusive)");
        this.blue = blue;
        return this;
    }

    public Color setBlue(int blue) {
        if(blue < 0 || blue > 255) throw new IllegalArgumentException("blue must be between 0 and 255 (inclusive)");
        this.blue = (float)blue/255;
        return this;
    }

    public Color withAlpha(float alpha) {
        return this.copy().setAlpha(alpha);
    }

    public Color withAlpha(int alpha) {
        return this.copy().setAlpha(alpha);
    }

    public Color withRed(float red) {
        return this.copy().setRed(red);
    }

    public Color withRed(int red) {
        return this.copy().setRed(red);
    }

    public Color withGreen(float green) {
        return this.copy().setGreen(green);
    }

    public Color withGreen(int green) {
        return this.copy().setGreen(green);
    }

    public Color withBlue(float blue) {
        return this.copy().setBlue(blue);
    }

    public Color withBlue(int blue) {
        return this.copy().setBlue(blue);
    }

}
