/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 *
 * @author alex
 */
public class Utils {
    
    /**
     * A helper to hide the difference between being in and being out of a jar.
     * @param resource
     * @return The resource URL
     */
    public static URL getResource(String resource) {
        return ClasspathResourceLoader.class.getResource(resource);
    }
    
    /**
     * A helper to hide the difference between being in and being out of a jar.
     * @param resource
     * @return The resource stream
     */
    public static InputStream getResourceAsStream(String resource) {
        return ClasspathResourceLoader.class.getResourceAsStream(resource);
    }

    /**
     * Handle velocity template loader frome either resource or file.
     * @return 
     */
    public static Properties getVelocityProperties() {
        Properties p = new Properties();
        p.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        p.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        return p;
    }

    public static void browse(final String url) {
        try {
                Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException e)  {
           new Thread(new Runnable() {
                @Override
                public void run() {
                    Process p;
                    try {
                        System.out.println("chromium "+url);
                        p = Runtime.getRuntime().exec("chromium "+url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        }
    }

    static void browse(URL url) {
        browse(url.toString());
    }
}
