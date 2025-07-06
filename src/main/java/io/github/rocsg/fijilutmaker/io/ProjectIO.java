package io.github.rocsg.fijilutmaker.io;

import java.io.File;

/**
 * Save and load full project sessions (colormap, images, config).
 */
public class ProjectIO {

    public static void saveProject(ProjectState state, File file) {
        // TODO: Serialize to disk (use Java serialization or JSON, as preferred)
    }

    public static ProjectState loadProject(File file) {
        // TODO: Deserialize from disk
        return null;
    }
}
