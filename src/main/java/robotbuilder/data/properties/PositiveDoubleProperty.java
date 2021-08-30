package robotbuilder.data.properties;

import robotbuilder.data.properties.DoubleProperty;
import robotbuilder.data.RobotComponent;

public class PositiveDoubleProperty extends DoubleProperty {

    public PositiveDoubleProperty() {
    }

    public PositiveDoubleProperty(String name, Object defaultValue, String[] validators, RobotComponent component, String value) {
        super(name, defaultValue, validators, component, value);
    }

    @Override
    public Property copy() {
        return new PositiveDoubleProperty(name, defaultValue, validators, component, value);
    }

    @Override
    public boolean isValid() {
            return super.isValid() && (Double)getValue() >= 0.0;
    }

    @Override
    public String getErrorMessage() {
        if (!super.isValid())
            return super.getErrorMessage();
        else if ((Double)getValue() < 0.0)
            return "name + \" expects a positive number. \"";
        else
            return super.getErrorMessage();
    }
}
