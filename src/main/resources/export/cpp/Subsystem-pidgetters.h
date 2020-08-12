#foreach ($component in $components)
#if ($component.getBase().getType() == "Command" && $subsystem_name == $component.getProperty("Requires").getValue() && $component.getProperty("Input").getValue())
        frc::PIDSource& Get#class($component.getProperty("Input").getValue())();

        frc::PIDOutput& Get#class($component.getProperty("Output").getValue())();
#end
#end
