
package robotbuilder.graph;

import java.util.HashMap;
import lombok.NonNull;

/**
 * Simple helper class to chain style settings.
 *
 * @author Sam Carlberg
 */
public class StyleMap extends HashMap<String, Object> {

    /**
     * Maps the value to the key.
     *
     * @param key the key. This cannot be null.
     * @param value the value. This cannot be null
     * @return this map, to allow for method chaining
     */
    public StyleMap set(@NonNull String key, @NonNull String value) {
        put(key, value);
        return this;
    }

    /**
     * Convenience method to map boolean values to keys.
     *
     * @see #set(java.lang.String, java.lang.String)
     */
    public StyleMap set(String key, boolean value) {
        return set(key, value ? "1" : "0");
    }

    /**
     * Convenience method to map number values to keys.
     *
     * @see #set(java.lang.String, java.lang.String)
     */
    public StyleMap set(String key, Number value) {
        return set(key, value.toString());
    }

}
