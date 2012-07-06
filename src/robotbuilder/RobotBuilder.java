
package robotbuilder;

import java.awt.EventQueue;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

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
                System.out.println(UIManager.getSystemLookAndFeelClassName());
                for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                    System.out.println(laf);
                }
                try {
                    if (!UIManager.getSystemLookAndFeelClassName().contains("MetalLookAndFeel"))
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(RobotBuilder.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(RobotBuilder.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(RobotBuilder.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(RobotBuilder.class.getName()).log(Level.SEVERE, null, ex);
                }
                MainFrame frame = MainFrame.getInstance();
                frame.openDefaultFile();
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
