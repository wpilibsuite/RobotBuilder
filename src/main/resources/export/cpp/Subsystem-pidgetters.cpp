#foreach ($component in $components)
#if ($component.getBase().getType() == "Command" && $subsystem-name == $component.getProperty("Requires").getValue() && $component.getProperty("Input").getValue())
        PIDSource #class($command.name)::get#class($component.getProperty("Input").getValue())() {
            return #variable($component.getProperty("Input").getValue());
        }

         PIDOutput #class($command.name)::get#class($component.getProperty("Output").getValue())() {
            return #variable($component.getProperty("Output").getValue());
        }
#end
#end
