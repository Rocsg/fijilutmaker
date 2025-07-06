package io.github.rocsg.fijilutmaker.model;

/**
 * Methods to check perceptual/quality aspects of a colormap (monotonicity, deltas, colorblind safety).
 */
public class ColormapValidator {

    public static boolean isMonotoneLuminance(Colormap c) {
        // TODO: Compute if luminance strictly increases/decreases along LUT.
        return true;
    }

    public static double[] luminanceProfile(Colormap c) {
        // TODO: Return luminance array for entire LUT.
        return new double[0];
    }

    public static double getPerceptualDelta(Colormap c) {
        // TODO: Compute average/max delta between steps (in Lab).
        return 0.0;
    }

    public static boolean isColorblindSafe(Colormap c) {
        // TODO: Analyze LUT for pairs of colors that may be confused.
        return true;
    }
}
