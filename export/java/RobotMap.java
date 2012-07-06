package ${package};
    
${helper.getImports($robot, "RobotMap")}
/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
    $helper.getDeclaration($component)
#end
#end

    public static void init() {
#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
        $helper.getConstructor($component)
        $helper.getExtra($component)
#end
#end
    }
}
