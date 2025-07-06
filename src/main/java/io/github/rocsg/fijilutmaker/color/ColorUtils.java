package io.github.rocsg.fijilutmaker.color;

/**
 * Color conversion utilities: RGB <-> Lab/LCH, luminance, gamut checks.
 * Reference: https://en.wikipedia.org/wiki/CIELAB_color_space
 */
public class ColorUtils {

    // D65 reference white
    private static final double REF_X = 95.047;
    private static final double REF_Y = 100.000;
    private static final double REF_Z = 108.883;

    /**
     * Convert RGB (0-255) to CIE Lab.
     * Returns {L*, a*, b*} (L in 0-100).
     */
    public static double[] rgbToLab(int r, int g, int b) {
        // 1. Convert RGB to [0,1]
        double rLin = pivotRGB(r / 255.0);
        double gLin = pivotRGB(g / 255.0);
        double bLin = pivotRGB(b / 255.0);

        // 2. RGB to XYZ (sRGB, D65)
        double x = rLin * 0.4124 + gLin * 0.3576 + bLin * 0.1805;
        double y = rLin * 0.2126 + gLin * 0.7152 + bLin * 0.0722;
        double z = rLin * 0.0193 + gLin * 0.1192 + bLin * 0.9505;

        // 3. XYZ scaled
        x = x * 100.0;
        y = y * 100.0;
        z = z * 100.0;

        // 4. XYZ to Lab
        double fx = pivotXYZ(x / REF_X);
        double fy = pivotXYZ(y / REF_Y);
        double fz = pivotXYZ(z / REF_Z);

        double L = 116.0 * fy - 16.0;
        double a = 500.0 * (fx - fy);
        double bLab = 200.0 * (fy - fz);

        return new double[] {L, a, bLab};
    }

    /**
     * Convert CIE Lab to RGB (0-255). May clip values if out of gamut.
     */
    public static int[] labToRgb(double L, double a, double bLab) {
        // 1. Lab to XYZ
        double fy = (L + 16.0) / 116.0;
        double fx = a / 500.0 + fy;
        double fz = fy - bLab / 200.0;

        double xr = invPivotXYZ(fx);
        double yr = invPivotXYZ(fy);
        double zr = invPivotXYZ(fz);

        double X = xr * REF_X;
        double Y = yr * REF_Y;
        double Z = zr * REF_Z;

        // 2. XYZ to RGB (sRGB)
        X = X / 100.0;
        Y = Y / 100.0;
        Z = Z / 100.0;

        double rLin = X *  3.2406 + Y * -1.5372 + Z * -0.4986;
        double gLin = X * -0.9689 + Y *  1.8758 + Z *  0.0415;
        double bLin = X *  0.0557 + Y * -0.2040 + Z *  1.0570;

        int r = toSRGB(rLin);
        int g = toSRGB(gLin);
        int b = toSRGB(bLin);

        return new int[] {r, g, b};
    }

    /**
     * Returns luminance Y (0–100) from RGB.
     */
    public static double luminance(int r, int g, int b) {
        // Use linear RGB → Y in XYZ
        double rLin = pivotRGB(r / 255.0);
        double gLin = pivotRGB(g / 255.0);
        double bLin = pivotRGB(b / 255.0);

        double y = rLin * 0.2126 + gLin * 0.7152 + bLin * 0.0722;
        return y * 100.0;
    }

    /**
     * Convert RGB to LCH (Lab cylindrical).
     */
    public static double[] rgbToLch(int r, int g, int b) {
        double[] lab = rgbToLab(r, g, b);
        double L = lab[0];
        double C = Math.sqrt(lab[1]*lab[1] + lab[2]*lab[2]);
        double H = Math.toDegrees(Math.atan2(lab[2], lab[1]));
        if (H < 0) H += 360.0;
        return new double[] {L, C, H};
    }

    /**
     * Convert LCH (Lab cylindrical) to RGB.
     */
    public static int[] lchToRgb(double L, double C, double H) {
        double hRad = Math.toRadians(H);
        double a = C * Math.cos(hRad);
        double bLab = C * Math.sin(hRad);
        return labToRgb(L, a, bLab);
    }

    /**
     * Returns true if RGB is displayable in sRGB (0-255).
     */
    public static boolean isDisplayable(int[] rgb) {
        for (int v : rgb) if (v < 0 || v > 255) return false;
        return true;
    }

    // Helper: sRGB gamma correction
    private static double pivotRGB(double v) {
        return (v <= 0.04045) ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
    }
    private static int toSRGB(double v) {
        v = (v <= 0.0031308) ? 12.92 * v : 1.055 * Math.pow(v, 1/2.4) - 0.055;
        int out = (int)Math.round(v * 255.0);
        return Math.max(0, Math.min(255, out));
    }
    // Helper: XYZ<->Lab
    private static double pivotXYZ(double t) {
        return (t > 0.008856) ? Math.cbrt(t) : (7.787 * t + 16.0/116.0);
    }
    private static double invPivotXYZ(double ft) {
        double ft3 = ft*ft*ft;
        return (ft3 > 0.008856) ? ft3 : (ft - 16.0/116.0) / 7.787;
    }
}
