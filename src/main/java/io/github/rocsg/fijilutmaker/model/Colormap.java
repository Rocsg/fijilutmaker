package io.github.rocsg.fijilutmaker.model;

import io.github.rocsg.fijilutmaker.model.Colormap.InterpolationMode;
import io.github.rocsg.fijilutmaker.utils.Interpolator;
import java.util.*;
import java.io.*;

/**
 * Model class for a Colormap/Lookup Table.
 */
public class Colormap {

    public enum InterpolationMode { LINEAR_RGB, SPLINE_RGB, LINEAR_LAB, SPLINE_LAB, LINEAR_LCH, SPLINE_LCH }

    private List<ColorPoint> points=new ArrayList<>();
    private InterpolationMode mode;
    private int lutSize;

    public Colormap() {
        this.points = new ArrayList<>();
        this.mode = InterpolationMode.LINEAR_RGB;
        this.lutSize = 256;
    }



    public static void main(String[] args) throws Exception {
        Colormap cm = new Colormap();
        cm.addPoint(new ColorPoint(0.0, new int[]{0,0,0}));
        cm.addPoint(new ColorPoint(1.0, new int[]{255,255,255}));
        cm.setInterpolationMode(Colormap.InterpolationMode.LINEAR_RGB);

        int[][] lut = cm.generateLUT();
        for (int i = 0; i < lut.length; i++) {
            System.out.println(Arrays.toString(lut[i]));
        }

        System.out.println("First: " + Arrays.toString(lut[0]));
        System.out.println("Last: " + Arrays.toString(lut[lut.length-1]));

        // Export
        cm.exportAsImageJLUT(new java.io.File("test.lut"));
    }


    public List<ColorPoint> getPoints() {
        return points; // <-- Rends la liste modifiable pour l'édition interactive
    }

    public void setInterpolationMode(InterpolationMode mode) { this.mode = mode; }
    public InterpolationMode getInterpolationMode() { return mode; }
    public void setLutSize(int size) { this.lutSize = size; }
    public int getLutSize() { return lutSize; }

    public void setPoints(List<ColorPoint> pts) { this.points = new ArrayList<>(pts); }

    public void addPoint(ColorPoint pt) { points.add(pt); }
    public void removePoint(int idx) {
        if (points.size() > 2 && idx >= 0 && idx < points.size()) {
            points.remove(idx);
        }
    }
    /**
     * Generate the LUT as int[256][3] (RGB 0-255).
     */
    public int[][] generateLUT() {
        int[][] lut = new int[lutSize][3];
        List<ColorPoint> pts = getSortedPoints();
        for (int i = 0; i < lutSize; i++) {
            double t = (double)i / (lutSize-1);
            int[] rgb;
            switch (mode) {
                case LINEAR_RGB:   rgb = Interpolator.linearRGB(pts, t); break;
                case SPLINE_RGB:   rgb = Interpolator.splineRGB(pts, t); break;
                case LINEAR_LAB:   rgb = Interpolator.linearLab(pts, t); break;
                case SPLINE_LAB:   rgb = Interpolator.splineLab(pts, t); break;
                case LINEAR_LCH:   rgb = Interpolator.linearLch(pts, t); break;
                case SPLINE_LCH:   rgb = Interpolator.splineLch(pts, t); break;
                default:           rgb = Interpolator.linearRGB(pts, t); break;
            }
            lut[i] = rgb;
        }
        return lut;
    }

    private List<ColorPoint> getSortedPoints() {
        List<ColorPoint> out = new ArrayList<>(points);
        out.sort(Comparator.comparingDouble(ColorPoint::getPosition));
        return out;
    }

    /**
     * Export LUT to ImageJ .lut binary file (R then G then B, 256 bytes each).
     */
    public void exportAsImageJLUT(File file) throws IOException {
        int[][] lut = generateLUT();
        if (lut.length != 256) throw new IOException("ImageJ LUT requires 256 entries");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Write all reds
            for (int i = 0; i < 256; i++) fos.write(lut[i][0]);
            // Write all greens
            for (int i = 0; i < 256; i++) fos.write(lut[i][1]);
            // Write all blues
            for (int i = 0; i < 256; i++) fos.write(lut[i][2]);
        }
    }

    /**
     * Utility: import LUT from ImageJ .lut binary file (R G B blocks of 256 bytes).
     * Sets 3 points: first, middle, last (simple use case).
     */
    public static Colormap importFromImageJLUT(File file) throws IOException {
        byte[] bytes = new byte[256*3];
        try (FileInputStream fis = new FileInputStream(file)) {
            if (fis.read(bytes) != 768)
                throw new IOException("Not enough bytes for ImageJ LUT");
        }
        Colormap cmap = new Colormap();
        int[][] lut = new int[256][3];
        for (int i = 0; i < 256; i++) {
            lut[i][0] = bytes[i] & 0xFF;         // Red
            lut[i][1] = bytes[256 + i] & 0xFF;   // Green
            lut[i][2] = bytes[512 + i] & 0xFF;   // Blue
        }
        // Only set first, middle, last as keypoints for edition
        cmap.addPoint(new ColorPoint(0.0, lut[0]));
        cmap.addPoint(new ColorPoint(0.5, lut[128]));
        cmap.addPoint(new ColorPoint(1.0, lut[255]));
        return cmap;
    }
}
