// The robot's subsystems
#foreach ($component in $components)
#if ($helper.exportsTo("RobotContainer", $component)
&& "#type($component)" != "" 
&& "#type($component)" == "SubsystemBase")
    #newObject($component)

#end
#end

// Joysticks
#foreach ($component in $components)
#if ($helper.exportsTo("OI", $component)
     && "#type($component)" != "" 
     && "#type($component)" == "Joystick")
    #newObject($component)

#end
#end