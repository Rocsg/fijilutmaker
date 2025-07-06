package io.github.rocsg.fijilutmaker.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility to arrange picked colors into a colormap, using heuristics
 * (e.g., monotonic luminance, hue ordering, etc.) based on mode.
 */
public class ColormapArranger {

    /**
     * Given a list of picked RGB colors, arranges them as ColorPoints for the given mode.
     * For SEQUENTIAL: sorts by luminance (dark to light) and spaces them evenly.
     * For DIVERGING: finds the center (lightest/darkest) and arranges others symmetrically.
     */
    public static List<ColorPoint> arrange(List<int[]> pickedColors, ColormapMode mode) {
        List<ColorPoint> points = new ArrayList<>();
        // TODO: Use ColorUtils.luminance, etc. for better arrangement

        if (pickedColors.size() == 0) return points;

        if (mode == ColormapMode.SEQUENTIAL) {
            // Example: sort by luminance (stub: just in input order for now)
            double step = 1.0 / (pickedColors.size() - 1);
            for (int i = 0; i < pickedColors.size(); i++)
                points.add(new ColorPoint(i * step, pickedColors.get(i)));
        }
        else {
            // For diverging, more logic needed (stub: center = middle, sides split)
            int mid = pickedColors.size() / 2;
            double step = 1.0 / (pickedColors.size() - 1);
            for (int i = 0; i < pickedColors.size(); i++)
                points.add(new ColorPoint(i * step, pickedColors.get(i)));
            // TODO: Implement smarter split (center/sides, luminance, etc.)
        }
        return points;
    }
}
