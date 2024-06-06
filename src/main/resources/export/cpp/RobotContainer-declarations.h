// Joysticks
#foreach ($component in $components)
#if ($helper.exportsTo("OI", $component)
     && "#type($component)" != "" 
     && "#base_type($component)" == "Joystick")
    #constructor($component)

#end
#end

frc::SendableChooser<frc2::Command*> m_chooser;
