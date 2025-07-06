package io.github.rocsg.fijilutmaker.model;

import io.github.rocsg.fijilutmaker.color.ColorUtils;

/**
 * Represents a key color and its normalized position in the colormap.
 * Position: [0.0, 1.0]
 * RGB: int[3] values in [0, 255]
 * Lab: float[3]
 */
public class ColorPoint {

    private double position;
    private int[] rgb;     // [r, g, b], [0,255]
    private float[] lab;   // [L,a,b]

    // Constructeur RGB (valeur de référence)
    public ColorPoint(double position, int[] rgb) {
        setPosition(position);
        setRgb(rgb);
    }

    // Constructeur Lab (peu utile en pratique, mais pour complétude)
    public ColorPoint(double position, float[] lab) {
        setPosition(position);
        setLab(lab);
    }

    // Constructeur avancé (pour usage avancé : attention à la cohérence)
    public ColorPoint(double position, int[] rgb, float[] lab) {
        setPosition(position);
        this.rgb = rgb.clone();
        this.lab = lab.clone();
    }

    public double getPosition() { return position; }
    public void setPosition(double pos) {
        if (pos < 0.0 || pos > 1.0)
            throw new IllegalArgumentException("Position must be in [0,1]");
        this.position = pos;
    }

    public int[] getRgb() { return rgb.clone(); }
    public void setRgb(int[] rgb) {
        if (rgb == null || rgb.length != 3)
            throw new IllegalArgumentException("RGB array must have length 3");
        for (int v : rgb)
            if (v < 0 || v > 255)
                throw new IllegalArgumentException("RGB values must be in [0,255]");
        this.rgb = rgb.clone();
        this.lab = ColorUtils.rgb2labFloat(this.rgb);
    }

    public float[] getLab() { return lab.clone(); }
    public void setLab(float[] lab) {
        if (lab == null || lab.length != 3)
            throw new IllegalArgumentException("Lab array must have length 3");
        this.lab = lab.clone();
        this.rgb = ColorUtils.lab2rgb(this.lab);
    }

    // Synchronisation manuelle si jamais on fait une modif "brute"
    public void updateLab() {
        this.lab = ColorUtils.rgb2labFloat(this.rgb);
    }
    public void updateRgb() {
        this.rgb = ColorUtils.lab2rgb(this.lab);
    }
}
