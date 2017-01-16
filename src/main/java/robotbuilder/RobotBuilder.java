
package robotbuilder;

import java.awt.EventQueue;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import robotbuilder.extensions.Extensions;

/**
 *
 * @author brad
 * @author Sam Carlberg
 */
public class RobotBuilder {

    public static final int VERSION_MAJOR = 2;
    public static final int VERSION_MINOR = 0;

    public static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR + "BB";

    public static final String SAVE_FILE_TYPE = "yaml";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            // All files should use the standard windows line endings to
            // maximize compatibility.
            System.setProperty("line.separator", "\r\n");

            try {
                if (!UIManager.getSystemLookAndFeelClassName().contains("MetalLookAndFeel")) {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Logger.getLogger(RobotBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
            Extensions.init();
            MainFrame frame = MainFrame.getInstance();
            frame.openDefaultFile();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
