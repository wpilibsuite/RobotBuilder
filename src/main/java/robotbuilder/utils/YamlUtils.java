package robotbuilder.utils;

import lombok.experimental.UtilityClass;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import robotbuilder.RobotBuilder;
import robotbuilder.data.DistinctValidator;
import robotbuilder.data.ExistsValidator;
import robotbuilder.data.ListValidator;
import robotbuilder.data.PaletteComponent;
import robotbuilder.data.UniqueValidator;
import robotbuilder.data.Validator;
import robotbuilder.data.properties.*;
import robotbuilder.exporters.ExportFile;

@UtilityClass
public class YamlUtils {

    public static final Yaml yaml;

    static {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setMaxAliasesForCollections(100);
        TagInspector taginspector = tag ->
                tag.getClassName().equals(StringProperty.class.getName()) ||
                tag.getClassName().equals(BooleanProperty.class.getName()) ||
                tag.getClassName().equals(IntegerProperty.class.getName()) ||
                tag.getClassName().equals(DoubleProperty.class.getName()) ||
                tag.getClassName().equals(PositiveDoubleProperty.class.getName()) ||
                tag.getClassName().equals(FileProperty.class.getName()) ||
                tag.getClassName().equals(ChoicesProperty.class.getName()) ||
                tag.getClassName().equals(ChildSelectionProperty.class.getName()) ||
                tag.getClassName().equals(TypeSelectionProperty.class.getName()) ||
                tag.getClassName().equals(ComponentSelectionProperty.class.getName()) ||
                tag.getClassName().equals(ParentProperty.class.getName()) ||
                tag.getClassName().equals(ParametersProperty.class.getName()) ||
                tag.getClassName().equals(ParameterSetProperty.class.getName()) ||
                tag.getClassName().equals(ConstantsProperty.class.getName()) ||
                tag.getClassName().equals(ListProperty.class.getName()) ||
                tag.getClassName().equals(TeamNumberProperty.class.getName()) ||
                tag.getClassName().equals(ParameterDescriptor.class.getName()) ||

                tag.getClassName().equals(DistinctValidator.class.getName()) ||
                tag.getClassName().equals(ExistsValidator.class.getName()) ||
                tag.getClassName().equals(UniqueValidator.class.getName()) ||
                tag.getClassName().equals(ListValidator.class.getName());

        loaderOptions.setTagInspector(taginspector);
        SafeConstructor constructor = new SafeConstructor(loaderOptions);

        constructor.addTypeDescription(new TypeDescription(PaletteComponent.class, "!Component"));

        // Properties
        constructor.addTypeDescription(new TypeDescription(StringProperty.class, "!StringProperty"));
        constructor.addTypeDescription(new TypeDescription(BooleanProperty.class, "!BooleanProperty"));
        constructor.addTypeDescription(new TypeDescription(IntegerProperty.class, "!IntegerProperty"));
        constructor.addTypeDescription(new TypeDescription(DoubleProperty.class, "!DoubleProperty"));
        constructor.addTypeDescription(new TypeDescription(PositiveDoubleProperty.class, "!PositiveDoubleProperty"));
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
        constructor.addTypeDescription(new TypeDescription(TeamNumberProperty.class, "!TeamNumberProperty"));
        constructor.addTypeDescription(new TypeDescription(ParameterDescriptor.class, "!ParameterDescriptor"));

        constructor.addTypeDescription(new TypeDescription(DistinctValidator.class, "!DistinctValidator"));
        constructor.addTypeDescription(new TypeDescription(ExistsValidator.class, "!ExistsValidator"));
        constructor.addTypeDescription(new TypeDescription(UniqueValidator.class, "!UniqueValidator"));
        constructor.addTypeDescription(new TypeDescription(ListValidator.class, "!ListValidator"));

        constructor.addTypeDescription(new TypeDescription(ExportFile.class, "!File"));

        constructor.addTypeDescription(new TypeDescription(CodeFileUtils.FileParser.class, "!Parser"));
        constructor.addTypeDescription(new TypeDescription(CodeFileUtils.TextFilter.class, "!Filter"));

        yaml = new Yaml(constructor);
    }

    public <T> T load(String yamlText) {
        return (T) yaml.load(yamlText);
    }

    public <T> String dump(T object) {
        return yaml.dump(object);
    }

}