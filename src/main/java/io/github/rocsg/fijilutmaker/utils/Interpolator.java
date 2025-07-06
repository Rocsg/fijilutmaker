package io.github.rocsg.fijilutmaker.utils;

import io.github.rocsg.fijilutmaker.model.ColorPoint;
import io.github.rocsg.fijilutmaker.color.ColorUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import java.util.List;

/**
 * Utility class for color interpolation: linear and cubic spline in RGB, Lab, and LCH.
 */
public class Interpolator {

    /**
     * Linear interpolation in RGB.
     */
    public static int[] linearRGB(List<ColorPoint> points, double t) {
        return interpolateRGB(points, t, false);
    }

    /**
     * Spline interpolation in RGB.
     */
    public static int[] splineRGB(List<ColorPoint> points, double t) {
        return interpolateRGB(points, t, true);
    }

    /**
     * Linear interpolation in Lab.
     */
    public static int[] linearLab(List<ColorPoint> points, double t) {
        return interpolateLab(points, t, false);
    }

    /**
     * Spline interpolation in Lab.
     */
    public static int[] splineLab(List<ColorPoint> points, double t) {
        return interpolateLab(points, t, true);
    }

    /**
     * Linear interpolation in LCH.
     */
    public static int[] linearLch(List<ColorPoint> points, double t) {
        return interpolateLch(points, t, false);
    }

    /**
     * Spline interpolation in LCH, with circular interpolation for Hue.
     */
    public static int[] splineLch(List<ColorPoint> points, double t) {
        return interpolateLch(points, t, true);
    }

    // ---- Private helpers ----

    private static int[] interpolateRGB(List<ColorPoint> points, double t, boolean spline) {
        int n = points.size();
        double[] positions = new double[n];
        double[][] ch = new double[3][n]; // r, g, b

        for (int i = 0; i < n; i++) {
            positions[i] = points.get(i).getPosition();
            int[] rgb = points.get(i).getRgb();
            for (int c = 0; c < 3; c++) ch[c][i] = rgb[c];
        }
        double[] result = new double[3];
        for (int c = 0; c < 3; c++) {
            result[c] = (spline && n > 2)
                ? splineInterp(positions, ch[c], t)
                : linearInterp(positions, ch[c], t);
        }
        int[] out = new int[3];
        for (int c = 0; c < 3; c++) out[c] = clamp((int)Math.round(result[c]), 0, 255);
        return out;
    }

    private static int[] interpolateLab(List<ColorPoint> points, double t, boolean spline) {
        int n = points.size();
        double[] positions = new double[n];
        double[][] lab = new double[3][n];
        for (int i = 0; i < n; i++) {
            positions[i] = points.get(i).getPosition();
            double[] labPt = ColorUtils.rgbToLab(
                points.get(i).getRgb()[0], points.get(i).getRgb()[1], points.get(i).getRgb()[2]);
            for (int c = 0; c < 3; c++) lab[c][i] = labPt[c];
        }
        double[] result = new double[3];
        for (int c = 0; c < 3; c++) {
            result[c] = (spline && n > 2)
                ? splineInterp(positions, lab[c], t)
                : linearInterp(positions, lab[c], t);
        }
        return ColorUtils.labToRgb(result[0], result[1], result[2]);
    }

    private static int[] interpolateLch(List<ColorPoint> points, double t, boolean spline) {
        int n = points.size();
        double[] positions = new double[n];
        double[][] lch = new double[3][n];
        for (int i = 0; i < n; i++) {
            positions[i] = points.get(i).getPosition();
            double[] lchPt = ColorUtils.rgbToLch(
                points.get(i).getRgb()[0], points.get(i).getRgb()[1], points.get(i).getRgb()[2]);
            lch[0][i] = lchPt[0]; // L
            lch[1][i] = lchPt[1]; // C
            lch[2][i] = Math.toRadians(lchPt[2]); // Hue in radians!
        }
        double L, C, H;
        if (spline && n > 2) {
            L = splineInterp(positions, lch[0], t);
            C = splineInterp(positions, lch[1], t);
            H = splineAngleInterp(positions, lch[2], t); // radians
        } else {
            L = linearInterp(positions, lch[0], t);
            C = linearInterp(positions, lch[1], t);
            H = linearAngleInterp(positions, lch[2], t);
        }
        double Hdeg = (Math.toDegrees(H) + 360.0) % 360.0;
        return ColorUtils.lchToRgb(L, C, Hdeg);
    }

    // ---- Core 1D interpolators ----

    /** Linear interpolation for non-angles. */
    private static double linearInterp(double[] x, double[] y, double t) {
        int n = x.length;
        if (t <= x[0]) return y[0];
        if (t >= x[n-1]) return y[n-1];
        for (int i = 1; i < n; i++) {
            if (x[i] >= t) {
                double alpha = (t - x[i-1]) / (x[i] - x[i-1]);
                return y[i-1]*(1-alpha) + y[i]*alpha;
            }
        }
        return y[n-1];
    }

    /** Linear interpolation for angles in radians (shortest path). */
    private static double linearAngleInterp(double[] x, double[] yRad, double t) {
        int n = x.length;
        if (t <= x[0]) return yRad[0];
        if (t >= x[n-1]) return yRad[n-1];
        for (int i = 1; i < n; i++) {
            if (x[i] >= t) {
                double a0 = yRad[i-1], a1 = yRad[i];
                double delta = angleDiff(a0, a1);
                double alpha = (t - x[i-1]) / (x[i] - x[i-1]);
                return (a0 + alpha * delta + 2*Math.PI) % (2*Math.PI);
            }
        }
        return yRad[n-1];
    }

    /** Spline interpolation for non-angles. */
    private static double splineInterp(double[] x, double[] y, double t) {
        PolynomialSplineFunction spline = new SplineInterpolator().interpolate(x, y);
        return spline.value(clamp(t, x[0], x[x.length-1]));
    }

    /** Spline interpolation for angles (in radians, shortest path, on circle). */
    private static double splineAngleInterp(double[] x, double[] yRad, double t) {
        // Unwrap angles to avoid discontinuities
        double[] unwrapped = new double[yRad.length];
        unwrapped[0] = yRad[0];
        for (int i = 1; i < yRad.length; i++) {
            double prev = unwrapped[i-1];
            double raw = yRad[i];
            double diff = angleDiff(prev, raw);
            unwrapped[i] = prev + diff;
        }
        PolynomialSplineFunction spline = new SplineInterpolator().interpolate(x, unwrapped);
        double val = spline.value(clamp(t, x[0], x[x.length-1]));
        // Wrap result to [0, 2PI)
        return (val % (2*Math.PI) + 2*Math.PI) % (2*Math.PI);
    }

    /** Returns the minimal difference a1->a2 in radians (signed, shortest path, [-π,π]) */
    private static double angleDiff(double a1, double a2) {
        double diff = (a2 - a1) % (2*Math.PI);
        if (diff > Math.PI) diff -= 2*Math.PI;
        if (diff < -Math.PI) diff += 2*Math.PI;
        return diff;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
