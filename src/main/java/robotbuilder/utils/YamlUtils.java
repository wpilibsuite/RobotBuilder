package robotbuilder.utils;

import lombok.experimental.UtilityClass;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import robotbuilder.data.DistinctValidator;
import robotbuilder.data.ExistsValidator;
import robotbuilder.data.ListValidator;
import robotbuilder.data.PaletteComponent;
import robotbuilder.data.UniqueValidator;
import robotbuilder.data.Validator;
import robotbuilder.data.properties.BooleanProperty;
import robotbuilder.data.properties.ChildSelectionProperty;
import robotbuilder.data.properties.ChoicesProperty;
import robotbuilder.data.properties.ComponentSelectionProperty;
import robotbuilder.data.properties.ConstantsProperty;
import robotbuilder.data.properties.DoubleProperty;
import robotbuilder.data.properties.FileProperty;
import robotbuilder.data.properties.IntegerProperty;
import robotbuilder.data.properties.ListProperty;
import robotbuilder.data.properties.ParameterSetProperty;
import robotbuilder.data.properties.ParametersProperty;
import robotbuilder.data.properties.ParentProperty;
import robotbuilder.data.properties.StringProperty;
import robotbuilder.data.properties.TypeSelectionProperty;

@UtilityClass
public class YamlUtils {

    public static final Yaml yaml;

    static {
        Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(PaletteComponent.class, "!Component"));

        // Properties
        constructor.addTypeDescription(new TypeDescription(StringProperty.class, "!StringProperty"));
        constructor.addTypeDescription(new TypeDescription(BooleanProperty.class, "!BooleanProperty"));
        constructor.addTypeDescription(new TypeDescription(IntegerProperty.class, "!IntegerProperty"));
        constructor.addTypeDescription(new TypeDescription(DoubleProperty.class, "!DoubleProperty"));
        constructor.addTypeDescription(new TypeDescription(FileProperty.class, "!FileProperty"));
        constructor.addTypeDescription(new TypeDescription(ChoicesProperty.class, "!ChoicesProperty"));
        constructor.addTypeDescription(new TypeDescription(ChildSelectionProperty.class, "!ChildSelectionProperty"));
        constructor.addTypeDescription(new TypeDescription(TypeSelectionProperty.class, "!TypeSelectionProperty"));
        constructor.addTypeDescription(new TypeDescription(ComponentSelectionProperty.class, "!ComponentSelectionProperty"));
        constructor.addTypeDescription(new TypeDescription(ParentProperty.class, "!ParentProperty"));
        constructor.addTypeDescription(new TypeDescription(ParametersProperty.class, "!Parameters"));
        constructor.addTypeDescription(new TypeDescription(ParameterSetProperty.class, "!ParameterSet"));
        constructor.addTypeDescription(new TypeDescription(ConstantsProperty.class, "!ConstantsProperty"));
        constructor.addTypeDescription(new TypeDescription(ListProperty.class, "!ListProperty"));

        constructor.addTypeDescription(new TypeDescription(DistinctValidator.class, "!DistinctValidator"));
        constructor.addTypeDescription(new TypeDescription(ExistsValidator.class, "!ExistsValidator"));
        constructor.addTypeDescription(new TypeDescription(UniqueValidator.class, "!UniqueValidator"));
        constructor.addTypeDescription(new TypeDescription(ListValidator.class, "!ListValidator"));

        yaml = new Yaml(constructor);
    }

    public <T> T load(String yamlText) {
        return (T) yaml.load(yamlText);
    }

    public <T> String dump(T object) {
        return yaml.dump(object);
    }

}