package io.github.rocsg.fijilutmaker.color;

/**
 * DaltonizeUtils
 * Utilities for simulating color vision deficiency (deuteranopia, tritanopia).
 * See: Machado et al., 2009 - A Physiologically-based Model for Simulation of Color Vision Deficiency
 */
public class DaltonizeUtils {

    // Deuteranope simulation (R, G, B in [0,255])
    public static int[] rgbDeuteranope(int[] rgb) {
        double R = rgb[0], G = rgb[1], B = rgb[2];
        int r = clamp((0.625 * R + 0.375 * G));
        int g = clamp((0.7 * R + 0.3 * G));
        int b = clamp((B));
        return new int[]{r, g, b};
    }

    // Tritanope simulation (R, G, B in [0,255])
    public static int[] rgbTritanope(int[] rgb) {
        double R = rgb[0], G = rgb[1], B = rgb[2];
        int r = clamp((R));
        int g = clamp((0.95 * G + 0.05 * B));
        int b = clamp((0.433 * R + 0.567 * B));
        return new int[]{r, g, b};
    }

    // Clamp helper
    private static int clamp(double v) {
        return (int) Math.max(0, Math.min(255, Math.round(v)));
    }
}
