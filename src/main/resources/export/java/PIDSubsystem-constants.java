#if ($subsystem.base.name == "PID Subsystem")
//P I D Variables
private static final double kP = $subsystem.getProperty("P").getValue();
private static final double kI = $subsystem.getProperty("I").getValue();
private static final double kD = $subsystem.getProperty("D").getValue();
#end
#set( $subsystem = $helper.getByName($subsystem_name, $robot) )
#set( $constants = $subsystem.getProperty("Constants").getValue())
#foreach( $constant in $constants)
    #declare_constant( $constant )
#end
