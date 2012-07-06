/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
}
