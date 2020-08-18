// The robot's subsystems
#foreach ($component in $components)
#if ("#type($component)" != "" 
&& ("#type($component)" == "frc2::SubsystemBase"
     || "#type($component)" == "PIDSubsystem"))
    #class($component.getName())* m_#variable($component.getName());
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