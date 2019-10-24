
package robotbuilder;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
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

    public static final int VERSION_MAJOR = 3;
    public static final int VERSION_MINOR = 0;

    public static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR;

    public static final String WPILIB_VERSION = "2020";

    public static final String FRC_HOME = findFrcHome(WPILIB_VERSION);

    public static final String SAVE_FILE_TYPE = "yaml";



    public static void main(String[] args) {
        // Force GTK2 - GTK3 has issues
        // See https://github.com/wpilibsuite/RobotBuilder/issues/114
        System.setProperty("jdk.gtk.version", "2");
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
            try {
              frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Utils.getResource("/icons/robotbuilder.png")));
            } catch (Exception ex) {
              Logger.getLogger(RobotBuilder.class.getName()).log(Level.INFO, null, ex);
            }
            frame.setVisible(true);
        });
    }

    private static final String findFrcHome(String frcYear) {
        String frcHome = System.getenv("FRC_HOME");
        if (frcHome == null) {
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
            if (isWindows) {
                String publicFolder = System.getenv("PUBLIC");
                if (publicFolder == null) {
                    publicFolder = "C:\\Users\\Public";
                }
                frcHome = new File(publicFolder, "wpilib\\" + frcYear).getAbsolutePath();
            } else {
                String userFolder = System.getProperty("user.home");
                frcHome = new File(userFolder, "wpilib/" + frcYear).getAbsolutePath();
            }
        }
        return frcHome;
    }
}
