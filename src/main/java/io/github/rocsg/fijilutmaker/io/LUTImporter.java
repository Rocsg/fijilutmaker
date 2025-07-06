package io.github.rocsg.fijilutmaker.io;

import io.github.rocsg.fijilutmaker.model.Colormap;
import java.io.File;

/**
 * Import LUT from ImageJ .lut, .csv, etc. Returns Colormap objects.
 */
public class LUTImporter {

    public static Colormap importFromImageJ(File file) {
        // TODO: Read 768 bytes, create Colormap with evenly spaced ColorPoints.
        return null;
    }

    public static Colormap importFromCSV(File file) {
        // TODO: Parse csv, build Colormap.
        return null;
    }

    // Add: importFromNapari, importFromJson, etc., as needed.
}
