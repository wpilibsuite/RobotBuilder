/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.awt.Desktop;
import java.io.*;
import java.net.MalformedURLException;
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
    static String PATH = new File("").getAbsolutePath()+"/resources";
    
    /**
     * A helper to hide the difference between being in and being out of a jar.
     * @param resource
     * @return The resource URL
     */
    public static URL getResource(String resource) {
        URL ret = Utils.class.getResource(resource);
        if (ret == null) {
            try {
                return new URL("File://"+PATH+resource);
            } catch (MalformedURLException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
    
    /**
     * A helper to hide the difference between being in and being out of a jar.
     * @param resource
     * @return The resource stream
     */
    public static InputStream getResourceAsStream(String resource) {
        System.out.println("Loading Resource: "+resource);
        InputStream ret = Utils.class.getResourceAsStream(resource);
        if (ret == null) {
            try {
                System.out.println("Loading Resource: "+PATH+resource);
                return new FileInputStream(PATH+resource);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    /**
     * Handle velocity template loader frome either resource or file.
     * @return 
     */
    public static Properties getVelocityProperties() {
        Properties p = new Properties();
        if (isJarred()) {
            p.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            p.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            return p;
        } else {
            p.setProperty("file.resource.loader.path", PATH);
            return p;
        }
    }

    /**
     * @return Whether or not the code is running from a jar file
     */
    private static boolean isJarred() {
        return Utils.class.getResource("/PaletteDescription.yaml") != null;
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
                        System.out.println("firefox -new-tab "+url);
                        p = Runtime.getRuntime().exec("firefox -new-tab "+url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        }
    }
}
