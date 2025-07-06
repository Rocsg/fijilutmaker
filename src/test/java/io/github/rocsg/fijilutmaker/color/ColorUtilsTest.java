package io.github.rocsg.fijilutmaker.color;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ColorUtilsTest {

    @Test
    public void testRGBtoLabAndBack() {
        int[] rgb = {120, 200, 80};
        double[] lab = ColorUtils.rgbToLab(rgb[0], rgb[1], rgb[2]);
        int[] rgb2 = ColorUtils.labToRgb(lab[0], lab[1], lab[2]);
        // Accept a small tolerance, due to rounding/gamut
        for (int i = 0; i < 3; i++) {
            assertTrue(Math.abs(rgb[i] - rgb2[i]) < 2, "Roundtrip RGB->Lab->RGB for channel " + i);
        }
    }

    @Test
    public void testLuminanceOrder() {
        int[] black = {0, 0, 0};
        int[] white = {255, 255, 255};
        assertTrue(ColorUtils.luminance(black[0], black[1], black[2]) < ColorUtils.luminance(white[0], white[1], white[2]));
    }

    @Test
    public void testRGBtoLCHAndBack() {
        int[] rgb = {10, 150, 220};
        double[] lch = ColorUtils.rgbToLch(rgb[0], rgb[1], rgb[2]);
        int[] rgb2 = ColorUtils.lchToRgb(lch[0], lch[1], lch[2]);
        for (int i = 0; i < 3; i++) {
            assertTrue(Math.abs(rgb[i] - rgb2[i]) < 3, "Roundtrip RGB->LCH->RGB for channel " + i);
        }
    }
}
