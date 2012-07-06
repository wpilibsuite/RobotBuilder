
package robotbuilder;

import java.awt.EventQueue;
import java.io.File;
import java.util.Properties;
import javax.swing.JFrame;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

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
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.setVisible(true);
                
                Properties p = new Properties();
//                p.setProperty("file.resource.loader.path", (new File(".")).getAbsolutePath());
                p.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
                p.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
                Velocity.init(p);
            }
        });
    }
}
