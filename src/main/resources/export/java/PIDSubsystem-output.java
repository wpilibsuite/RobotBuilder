#set($subsystem = $helper.getByName($subsystem_name, $robot))
#foreach ($component in $components)
#if ($component.name == $subsystem.getProperty("Output").getValue() && $component.getSubsystem().equals($subsystem_name.concat(" ")))
#if($subsystem.getProperty("Limit Output").getValue())
        #variable($component.name).set(MathUtil.clamp(output, ${subsystem.getProperty("Minimum Output").getValue()}, ${subsystem.getProperty("Maximum Output").getValue()}));
#else
        #variable($component.name).set(output);
#end
#end
#end
