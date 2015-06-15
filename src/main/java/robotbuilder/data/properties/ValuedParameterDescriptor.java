
package robotbuilder.data.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import robotbuilder.Utils;

/**
 *
 * @author Sam Carlberg
 */
@Data
@NoArgsConstructor // required for yaml deserialization
@EqualsAndHashCode(callSuper = true)
public class ValuedParameterDescriptor extends ParameterDescriptor {

    /**
     * The value of this parameter. This can either be user-specified from
     * RobotBuilder, or can be set to reference some variable/method/expression
     * in the generated code.
     */
    private Object value;

    /**
     * Creates a new {@code ValuedParameterDescriptor} with the name and type of
     * the given parameter descriptor and the default value for it's type.
     */
    public ValuedParameterDescriptor(ParameterDescriptor other) {
        this(other.getName(), other.getType(), null);
    }

    public ValuedParameterDescriptor(String name, String type, Object value) {
        super(name, type);
        setValue(value);
    }

    /**
     * Sets the value to the default for the type of the parameter.
     * <ul>
     * <li><code>boolean</code> defaults to the boolean value
     * <code>false</code>.
     * <li>All number types ({@code byte}, {@code int}, {@code long}, and
     * {@code double}) default to zero.
     * <li><code>String</code> defaults to an empty string ("")
     * </ul>
     */
    public void setValueToDefault() {
        switch (getType()) {
            case "boolean":
                this.value = false;
                break;
            case "byte":
            case "int":
            case "long":
            case "double":
                this.value = "0";
                break;
            case "String":
                this.value = "";
                break;
        }
    }

    /**
     * Sets the value. If the given value is null, sets it to the default for
     * the type, otherwise sets the value to the given one.
     *
     * @param value the value to set
     * @see #setValueToDefault()
     */
    public void setValue(Object value) {
        if (value == null) {
            setValueToDefault();
        } else {
            this.value = value;
        }
    }

    /**
     * Checks if this is a valid description of a parameter. Checks if the name
     * is valid, the type is valid, and that the value matches the type.
     *
     * @return
     */
    @Override
    public boolean isValid() {
        return super.isValid() && valueMatchesType();
    }

    /**
     * Checks if the value matches with the type.
     */
    public boolean valueMatchesType() {
        switch (getType()) {
            case "boolean":
                return value instanceof Boolean;
            case "byte":
                return value instanceof String
                        && (isReference()
                        || (Utils.doesNotError(() -> Integer.parseInt((String) value))
                        // use Integer.parseInt instead of Byte.parseByte because C++ maximum value is > Java's maximum value
                        // and we have no way of knowing which language will be exported to
                        && Integer.parseInt((String) value) >= -128 // -128 is minimum value in Java (C++: 0)
                        && Integer.parseInt((String) value) < 256)); // 255 is maximum value in C++ (Java: 127)
            case "int":
                return value instanceof String
                        && (isReference()
                        || Utils.doesNotError(() -> Integer.parseInt((String) value)));
            case "long":
                return value instanceof String
                        && (isReference()
                        || Utils.doesNotError(() -> Long.parseLong((String) value)));
            case "double":
                return value instanceof String
                        && (isReference()
                        || Utils.doesNotError(() -> Double.parseDouble((String) value)));
            case "String":
                return value instanceof String
                        && isValidStringValue();
        }
        return false;
    }

    public boolean isReference() {
        if (value instanceof String) {
            String s = (String) value;
            return s.startsWith("$");
        }
        return false;
    }

    private boolean isValidStringValue() {
        String s = (String) value;
        return !s.contains("\\") && !s.contains("\""); // disallow backslashes and quotes
    }

    @Override
    public Object[] toArray() {
        return new Object[]{getName(), getType(), getValue()};
    }

    @Override
    public String toString() {
        return "(" + getName() + ", " + getType() + ", " + value + ")";
    }
}
