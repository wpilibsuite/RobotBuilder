package robotbuilder.data.properties;

import robotbuilder.data.RobotComponent;

public class TeamNumberProperty extends IntegerProperty {
    public TeamNumberProperty(String name, Object defaultValue, String[] validators, RobotComponent component, String value) {
        super(name, defaultValue, validators, component, value);
    }

    public TeamNumberProperty() {
    }

    @Override
    public boolean isValid() {
        try {
            // Check that it's a valid double
            Integer val = Integer.parseInt((value != null) ? value : defaultValue.toString());
            return super.isValid() && val > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    public Property copy() {
        return new TeamNumberProperty(name, defaultValue, validators, component, value);
    }

    @Override
    public String getErrorMessage() {
        if (isValid())
            return super.getErrorMessage();
        else
            return "invalid team number";
    }
}
