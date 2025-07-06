package io.github.rocsg.fijilutmaker.utils;

import io.github.rocsg.fijilutmaker.model.ColorPoint;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InterpolatorTest {

    @Test
    public void testLinearRGBExtremes() {
        List<ColorPoint> pts = Arrays.asList(
            new ColorPoint(0.0, new int[]{0,0,0}),
            new ColorPoint(1.0, new int[]{255,255,255})
        );
        assertArrayEquals(new int[]{0,0,0}, Interpolator.linearRGB(pts, 0.0));
        assertArrayEquals(new int[]{255,255,255}, Interpolator.linearRGB(pts, 1.0));
        int[] mid = Interpolator.linearRGB(pts, 0.5);
        for (int c = 0; c < 3; c++) assertTrue(mid[c] > 120 && mid[c] < 136);
    }

    @Test
    public void testSplineRGBSimple() {
        List<ColorPoint> pts = Arrays.asList(
            new ColorPoint(0.0, new int[]{0,0,0}),
            new ColorPoint(0.5, new int[]{128,0,128}),
            new ColorPoint(1.0, new int[]{255,255,255})
        );
        int[] start = Interpolator.splineRGB(pts, 0.0);
        int[] end   = Interpolator.splineRGB(pts, 1.0);
        int[] mid   = Interpolator.splineRGB(pts, 0.5);

        // Tolérance ±4 sur les composantes
        int[] expectedStart = new int[]{0,0,0};
        int[] expectedEnd   = new int[]{255,255,255};
        int[] expectedMid   = new int[]{128,0,128};

        for (int i = 0; i < 3; i++) {
            assertTrue(Math.abs(start[i] - expectedStart[i]) <= 4, "splineRGB start channel " + i);
            assertTrue(Math.abs(end[i]   - expectedEnd[i])   <= 4, "splineRGB end channel " + i);
            assertTrue(Math.abs(mid[i]   - expectedMid[i])   <= 4, "splineRGB mid channel " + i);
        }
    }

    @Test
    public void testLinearLabContinuity() {
        List<ColorPoint> pts = Arrays.asList(
            new ColorPoint(0.0, new int[]{255,0,0}),   // Red
            new ColorPoint(1.0, new int[]{0,0,255})    // Blue
        );
        int[] mid = Interpolator.linearLab(pts, 0.5);
        // Should be some purple (not black or white)
        System.out.println(Arrays.toString(mid));
        assertTrue(mid[0] < 220 && mid[2] > 100, "Midpoint should be purplish");
    }

    @Test
    public void testSplineLabThreePoints() {
        List<ColorPoint> pts = Arrays.asList(
            new ColorPoint(0.0, new int[]{255,0,0}),
            new ColorPoint(0.5, new int[]{0,255,0}),
            new ColorPoint(1.0, new int[]{0,0,255})
        );
        int[] at0   = Interpolator.splineLab(pts, 0.0);
        int[] at05  = Interpolator.splineLab(pts, 0.5);
        int[] at1   = Interpolator.splineLab(pts, 1.0);

        int[] exp0 = new int[]{255,0,0};
        int[] exp1 = new int[]{0,0,255};
        for (int i = 0; i < 3; i++) {
            assertTrue(Math.abs(at0[i] - exp0[i]) <= 10, "splineLab at0 channel " + i);
            assertTrue(Math.abs(at1[i] - exp1[i]) <= 10, "splineLab at1 channel " + i);
        }
        // At 0.5, green should be dominant
        assertTrue(at05[1] > at05[0] && at05[1] > at05[2]);
    }

    @Test
    public void testLinearLchHueWrap() {
        // Hue near 0° and near 100°, should interpolate shortest way
        List<ColorPoint> pts = Arrays.asList(
            new ColorPoint(0.0, new int[]{255,0,0}),   // Hue ~ 40°
            new ColorPoint(1.0, new int[]{255,255,0})  // Hue ~ 100°
        );
        int[] mid = Interpolator.linearLch(pts, 0.5);
        // Should be yellowish orange (R and G both high)
        assertTrue(mid[0] > 200 && mid[1] > 100, "linearLch midpoint should be yellowish");
    }

    @Test
    public void testSplineLchCircular() {
        // Red (0°) to Magenta (~300°), should interpolate through purple
        List<ColorPoint> pts = Arrays.asList(
            new ColorPoint(0.0, new int[]{255,0,0}),     // Red
            new ColorPoint(0.5, new int[]{255,0,255}),   // Magenta
            new ColorPoint(1.0, new int[]{255,255,0})    // Yellow
        );
        int[] mag = Interpolator.splineLch(pts, 0.5);
        // Should be close to magenta (R and B both high, G low)
        assertTrue(mag[0] > 200 && mag[2] > 200 && mag[1] < 100, "splineLch mid should be magenta-ish");
    }
}
