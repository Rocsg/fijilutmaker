package io.github.rocsg.fijilutmaker.ui;

import org.scijava.plugin.Plugin;
import org.scijava.command.Command;
import javax.swing.SwingUtilities;

/**
 * Main entry point for Fiji/ImageJ plugin.
 * Opens the main application frame.
 */
@Plugin(type = Command.class, menuPath = "Plugins>Color>FijiLUTMaker")
public class FijiLUTMakerPlugin implements Command {
    public FijiLUTMakerPlugin () {
    }

    public void run() {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
    public static void main(String[] args) {
        new FijiLUTMakerPlugin().run();
    }
}
