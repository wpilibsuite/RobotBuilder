
package robotbuilder.utils;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.experimental.UtilityClass;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import robotbuilder.Utils;
import static robotbuilder.ActionsClass.EXPORTERS_PATH;

/**
 *
 * @author Sam Carlberg
 */
@UtilityClass
public class CodeFileUtils {

    /**
     * Class that uses a regex to parse information from a file.
     */
    @Data
    public static class FileParser implements Function<String, String> {

        /**
         * The type of the file to be parsed.
         */
        private String fileType;

        /**
         * The regex to use to parse data.
         */
        private String regex;

        /**
         * The number of the group in the regex that contains the desired
         * information. A group number of zero corresponds to the entire text
         * matched by the regex.
         */
        private int groupNumber;

        /**
         * An optional list of functions to remove text, such as comments,
         * before parsing. Functions are called in the order they appear in this
         * array.
         */
        private List<Function<String, String>> textFilters;

        @Override
        public String apply(String fileText) {
            for (Function<String, String> filter : textFilters) {
                fileText = filter.apply(fileText);
            }
            Matcher matcher = Pattern.compile(regex).matcher(fileText);
            if (matcher.find()) {
                return matcher.group(groupNumber);
            }
            return "";
        }

    }

    /**
     * Tiny class to let text filter functions be saved/loaded with yaml.
     */
    @Data
    public static class TextFilter implements Function<String, String> {

        /**
         * The regex matching the text to be removed. All text this matches will
         * be removed.
         */
        private String regex;

        @Override
        public String apply(String text) {
            return text.replaceAll(regex, "");
        }

    }

    private static final Map<String, Function<String, String>> textParserMap = new HashMap<>();
    private static final Map<String, Function<File, String>> fileParserMap = new HashMap<>();
    private static final TypeDescription fileParserDescriptor = new TypeDescription(FileParser.class, "!Parser");
    private static final TypeDescription textFilterDescriptor = new TypeDescription(TextFilter.class, "!Filter");
    private static final Constructor constructor = new Constructor();
    private static final Yaml yaml = new Yaml(constructor);

    static {
        try {
            init();
        } catch (Exception e) {
            Logger.getLogger(CodeFileUtils.class.getName())
                    .log(Level.SEVERE, "Could not initialize parsers. Is the yaml file malformed?", e);
        }
    }

    private static void init() {
        constructor.addTypeDescription(fileParserDescriptor);
        constructor.addTypeDescription(textFilterDescriptor);
        String path = EXPORTERS_PATH + "parsers.yaml";
        Map<String, Object> m = (Map) yaml.load(Utils.getResourceAsStream(path));
        List<FileParser> loadedParsers = (List<FileParser>) m.get("parsers");
        loadedParsers.forEach(parser -> {
            textParserMap.put(parser.getFileType(), parser);
            fileParserMap.put(parser.getFileType(), file -> parser.apply(Utils.getFileText(file)));
        });
    }

    /**
     * Gets the superclass of a C++ class. Special case because .cpp files don't
     * contain information about their class hierarchy.
     */
    private static String getSuperClassCpp(File file) {
        File headerFile = new File(file.getAbsolutePath().replace(".cpp", ".h"));
        return fileParserMap.get("h").apply(headerFile);
    }

    /**
     * Gets the superclass of class contained in the given file. This is used by
     * the Velocity engine to check if the type of the current save file is
     * different than what's going to be written to it. (fix for artf3715)
     */
    public static String getSavedSuperclass(File file) {
        String fileType = Utils.getFileExtension(file);
        if (!fileParserMap.containsKey(fileType)) {
            return ""; // not a supported file type
        }
        return fileParserMap.get(fileType).apply(file);
    }

    public static String getSavedSuperclass(String fileText) {
        String superClass = "";
        // We don't know the type of the file, so go through every parser to try
        // to get a hit
        for (Function<String, String> f : textParserMap.values()) {
            superClass = f.apply(fileText);
            if (!superClass.isEmpty()) {
                break;
            }
        }
        return superClass;
    }

}
