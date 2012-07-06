
package robotbuilder;

import java.awt.EventQueue;
import java.io.File;
import javax.swing.JFrame;
import org.apache.velocity.app.Velocity;

/**
 *
 * @author brad
 */
public class RobotBuilder {


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println(System.getProperty("java.class.path"));
                System.out.println((new File(".")).getAbsolutePath());
                MainFrame frame = MainFrame.getInstance();
                frame.openDefaultFile();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                
                Velocity.init();
            }
        });
    }
}
