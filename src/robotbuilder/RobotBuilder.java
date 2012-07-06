
package robotbuilder;

import java.awt.EventQueue;
import java.io.File;
import javax.swing.JFrame;

/**
 *
 * @author brad
 */
public class RobotBuilder {
    public static String VERSION = "0.0.1";

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println(System.getProperty("java.class.path"));
                System.out.println((new File(".")).getAbsolutePath());
                MainFrame frame = MainFrame.getInstance();
                frame.openDefaultFile();
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
