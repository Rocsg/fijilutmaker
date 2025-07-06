package io.github.rocsg.fijilutmaker.io;

import io.github.rocsg.fijilutmaker.model.Colormap;
import ij.ImagePlus;
import java.util.List;

/**
 * Serializable class to hold current project state (colormap, images, settings).
 */
public class ProjectState implements java.io.Serializable {
    public Colormap colormap;
    public List<ImagePlus> images;
    public String configJson; // Store other settings as needed

    // Constructor, getters, setters...
}
