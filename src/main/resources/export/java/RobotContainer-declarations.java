// The robot's subsystems
#foreach ($component in $components)
#if ($helper.exportsTo("RobotContainer", $component)
&& "#type($component)" != "" 
&& ("#type($component)" == "SubsystemBase"
     || "#type($component)" == "PIDSubsystem"))
    private final #class($component.getName()) m_#variable($component.getName()) = new #class($component.getName())();
#end
#end

// Joysticks
#foreach ($component in $components)
#if ($helper.exportsTo("OI", $component)
     && "#type($component)" != "" 
     && "#type($component)" == "Joystick")
    #constructor($component)

#end
#end