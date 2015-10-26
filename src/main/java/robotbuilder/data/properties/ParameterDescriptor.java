
package robotbuilder.data.properties;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Sam Carlberg
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParameterDescriptor implements Validatable, Serializable {

    public static final String[] SUPPORTED_TYPES
            = new String[]{"String", "int", "double", "boolean", "byte", "long"};

    /**
     * List of reserved keywords.
     */
    private static final List<String> reserved
            = Arrays.asList("boolean", "bool",
                    "byte", "char", "short", "int", "long", "float", "double",
                    "uint8_t", "uint16_t", "uint32_t", "uint64_t",
                    "String", "void", "class", "interface", "public", "protected", "private",
                    "static", "final", "const", "import", "package", "volatile", "transient", "synchronized",
                    "this", "super", "extends", "implements", "goto", "for", "while", "do", "instanceof",
                    "enum", "struct", "union", "typedef");

    private static final String DEFAULT_NAME = "[change me]";
    private static final String DEFAULT_TYPE = "double";

    /**
     * The name of the parameter. Defaults to "[change me]".
     *
     * @param name the name of the parameter
     * @return the name of the parameter.
     */
    private String name = DEFAULT_NAME;

    /**
     * The type of the parameter. Defaults to "String".
     *
     * @param type the type of the parameter
     * @return the type of the parameter
     */
    private String type = DEFAULT_TYPE;

    /**
     * Checks if this is a valid descriptor for a parameter. Defaults to
     * checking if the name is valid and the type is supported.
     */
    @Override
    public boolean isValid() {
        return isValidParamName(name)
                && Arrays.asList(SUPPORTED_TYPES).contains(type);
    }

    /**
     * Checks if the given name is valid. A name is valid if it:
     * <ul>
     * <li>Is not a reserved Java or C++ keyword (such as <code>class</code>)
     * <li>Only contains characters allowed in a valid Java or C++ identifier
     * (i.e. letters, numbers (after the first character), and underscores.
     * </ul>
     *
     * @param name the name to check
     * @return true if the name is valid, false if it is invalid
     */
    public static final boolean isValidParamName(String name) {
        return !reserved.contains(name)
                && name.matches("^[a-zA-Z_]+\\w*$"); // at least one letter or underscore to start and then any amount of letters, numbers, and underscores
    }

    /**
     * Creates an array representation of this ParameterDescriptor.
     */
    public Object[] toArray() {
        return new Object[]{getName(), getType()};
    }

}
