// The robot's subsystems
#foreach ($component in $components)
#if ("#type($component)" != "" 
&& ("#type($component)" == "frc2::SubsystemBase"
     || "#type($component)" == "frc2::PIDSubsystem"))
    #class($component.getName()) #variable($component.getName());
#end
#end

${Collections.reverse($components)}
#foreach( $component in $components )
#if ($helper.exportsTo("OI", $component) && "#prototype($component)" != "")
    #prototype($component)

#end
#end
