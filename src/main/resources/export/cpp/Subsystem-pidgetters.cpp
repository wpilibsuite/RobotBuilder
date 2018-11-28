#foreach ($component in $components)
#if ($component.getBase().getType() == "Command" && $subsystem-name == $component.getProperty("Requires").getValue() && $component.getProperty("Input").getValue())
        std::shared_ptr<frc::PIDSource> #class($subsystem-name)::Get#class($component.getProperty("Input").getValue())() {
            return #variable($component.getProperty("Input").getValue());
        }

         std::shared_ptr<frc::PIDOutput> #class($subsystem-name)::Get#class($component.getProperty("Output").getValue())() {
            return #variable($component.getProperty("Output").getValue());
        }
#end
#end
