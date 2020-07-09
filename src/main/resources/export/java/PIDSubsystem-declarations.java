#set($subsystem = $helper.getByName($subsystem_name, $robot))
#foreach ($component in $components)
#if ($component.subsystem == $subsystem.subsystem && $component != $subsystem)
    private final #constructor($component)
#end
#end

    //P I D Variables
    private static final double kP = $subsystem.getProperty("P").getValue();
    private static final double kI = $subsystem.getProperty("I").getValue();
    private static final double kD = $subsystem.getProperty("D").getValue();
    private static final double kF = $subsystem.getProperty("F").getValue();