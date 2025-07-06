package io.github.rocsg.fijilutmaker.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for global settings: mode selection, interpolation type, import/export.
 */
public class SettingsPanel extends JPanel {
    // TODO: Provide controls for colormap mode, interpolation, export/import.

    public SettingsPanel() {
        setBorder(BorderFactory.createTitledBorder("Settings"));
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel("Colormap mode, interpolation, export/import controls here."));
    }
}
