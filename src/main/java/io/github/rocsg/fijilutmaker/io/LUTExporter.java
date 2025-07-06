package io.github.rocsg.fijilutmaker.io;

import io.github.rocsg.fijilutmaker.model.Colormap;
import java.io.File;

/**
 * Export Colormap/LUT to ImageJ, CSV, or other formats.
 */
public class LUTExporter {

    public static void exportToImageJ(Colormap cmap, File file) {
        // TODO: Write as 768-byte .lut file.
    }

    public static void exportToCSV(Colormap cmap, File file) {
        // TODO: Write as csv (index, r, g, b).
    }

    // Add: exportToNapari, exportToPythonScript, etc.
}
