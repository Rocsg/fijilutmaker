package io.github.rocsg.fijilutmaker.color;

/**
 * Simulate color vision deficiency (color blindness) on RGB values.
 * Uses standard 3x3 matrices for Deuteranope, Tritanope, etc.
 */
public class ColorBlindSimulator {

    /**
     * Simulate Deuteranope (green-weak) on a single RGB triplet.
     */
    public static int[] simulateDeuteranope(int[] rgb) {
        // TODO: Matrix transform on RGB
        return rgb.clone();
    }

    /**
     * Simulate Tritanope (blue-weak) on a single RGB triplet.
     */
    public static int[] simulateTritanope(int[] rgb) {
        // TODO: Matrix transform
        return rgb.clone();
    }

    // Optionally, add simulateProtan (red-weak), etc.

    /**
     * Simulate CVD on a full LUT.
     */
    public static int[][] simulateLUT(int[][] lut, String type) {
        int[][] out = new int[lut.length][3];
        for (int i = 0; i < lut.length; i++) {
            switch (type.toLowerCase()) {
                case "deuteranope": out[i] = simulateDeuteranope(lut[i]); break;
                case "tritanope":   out[i] = simulateTritanope(lut[i]);   break;
                // Add more cases
                default:            out[i] = lut[i].clone();
            }
        }
        return out;
    }
}
