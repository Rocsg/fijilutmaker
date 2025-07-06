package io.github.rocsg.fijilutmaker.model;

import java.util.Arrays;

/**
 * Represents a key color and its normalized position in the colormap.
 * Position: [0.0, 1.0]
 * RGB: int[3] values in [0, 255]
 */
public class ColorPoint {

    private double position;
    private int[] rgb; // always [r, g, b], values in [0,255]
 

    public ColorPoint(double position, int[] rgb) {
        if (position < 0.0 || position > 1.0)
            throw new IllegalArgumentException("Position must be in [0,1]");
        if (rgb == null || rgb.length != 3)
            throw new IllegalArgumentException("RGB array must have length 3");
        for (int v : rgb)
            if (v < 0 || v > 255)
                throw new IllegalArgumentException("RGB values must be in [0,255]");
        this.position = position;
        this.rgb = new int[] {rgb[0], rgb[1], rgb[2]};
    }

    public double getPosition() { return position; }
    public void setPosition(double pos) {
        if (pos < 0.0 || pos > 1.0)
            throw new IllegalArgumentException("Position must be in [0,1]");
        this.position = pos;
    }

    public int[] getRgb() {
        return new int[] {rgb[0], rgb[1], rgb[2]};
    }

    public void setRgb(int[] rgb) {
        if (rgb == null || rgb.length != 3)
            throw new IllegalArgumentException("RGB array must have length 3");
        for (int v : rgb)
            if (v < 0 || v > 255)
                throw new IllegalArgumentException("RGB values must be in [0,255]");
        this.rgb = new int[] {rgb[0], rgb[1], rgb[2]};
    }
}
