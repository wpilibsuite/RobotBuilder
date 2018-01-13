
#foreach ($component in $components)
#if ($component.getBase().getType() == "Command" && $component.getProperty("Input").getValue() && $subsystem.subsystem.replace(' ', '') == $component.getProperty("Requires").getValue().replace(' ', ''))

    public PIDSource get#class($component.getProperty("Input").getValue())() {
      return #variable($component.getProperty("Input").getValue());

    }
    public PIDOutput get#class($component.getProperty("Output").getValue())() {
      return #variable($component.getProperty("Output").getValue());

    }

#end
#end
