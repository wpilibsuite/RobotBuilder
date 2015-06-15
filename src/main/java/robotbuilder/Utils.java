
package robotbuilder;

import java.awt.Desktop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.Files;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SerializationException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author alex
 * @author Sam Carlberg
 */
public class Utils {

    /**
     * A helper to hide the difference between being in and being out of a jar.
     *
     * @param resource
     * @return The resource URL
     */
    public static URL getResource(String resource) throws FileNotFoundException {
        URL url = ClasspathResourceLoader.class.getResource(resource);
        if (url == null) {
            throw new FileNotFoundException("Cannot load resource: " + resource);
        }
        return url;
    }

    /**
     * A helper to hide the difference between being in and being out of a jar.
     *
     * @param resource
     * @return The resource stream
     */
    public static InputStream getResourceAsStream(String resource) {
        return ClasspathResourceLoader.class.getResourceAsStream(resource);
    }

    /**
     * Handle velocity template loader frome either resource or file.
     *
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
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException e) {
            new Thread(() -> {
                Process p;
                try {
                    System.out.println("firefox " + url);
                    p = Runtime.getRuntime().exec("firefox " + url);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }).start();
        }
    }

    static void browse(URL url) {
        browse(url.toString());
    }

    /**
     * Checks if the given expression throws an error. This will not throw an
     * exception, nor will it log a thrown exception.
     *
     * @param expression the expression to check
     * @return true if the expression does not throw an error, false if it does
     */
    public static boolean doesNotError(Runnable expression) {
        try {
            expression.run();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean doesNotError(Callable<?> expression) {
        try {
            expression.call();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Performs a deep copy of the given object. Note that the object must have
     * a default (zero-argument) constructor.
     *
     * @param <T> the type of the object to copy and return
     * @param original the object to make a copy of
     * @return a copy of the given object
     */
    public static <T> T deepCopy(T original) {
        if (original == null) {
            return null;
        }
        Yaml y = new Yaml();
        String yaml = y.dump(original);
        return (T) y.load(yaml);
    }

    /**
     * Performs a deep copy of the given object. This method is preferable to
     * the more general version because that relies on the object having a
     * default (zero-argument) constructor; however, this method only works for
     * serializable objects.
     *
     * @param <T> the type of the object to copy and return
     * @param original the object to copy
     * @return a deep copy of the given object
     */
    public static <T extends Serializable> T deepCopy(T original) {
        if (original == null) {
            return null;
        }
        try {
            return (T) SerializationUtils.clone(original);
        } catch (SerializationException notSerializable) {
            return (T) deepCopy((Object) original);
        }
    }

    /**
     * Gets the text in the given file. An empty String is returned if there is
     * an error getting the text.
     */
    public static String getFileText(File file) {
        String text = "";
        try {
            text = String.join("\n", Files.lines(file.toPath()).toArray(String[]::new));
        } catch (IOException ex) {
            // Couldn't read from file, return empty String
        }
        return text;
    }

    /**
     * Gets the extension for the given file, or an empty String if it has no
     * extension. For example, calling this on /a/b/c.d will output "d", and
     * calling this on /x/y/z will output "".
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int dot = fileName.lastIndexOf('.');
        if (dot == -1) {
            return "";
        }
        return fileName.substring(dot + 1);
    }

}
