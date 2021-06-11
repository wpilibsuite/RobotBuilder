#set($subsystem = $helper.getByName($subsystem_name, $robot))
#foreach ($component in $components)
#if ($component.name == $subsystem.getProperty("Output").getValue())
    #if($subsystem.getProperty("Limit Output").getValue())
    #variable($component.name).Set(std::clamp(output, ${subsystem.getProperty("Minimum Output").getValue()}, ${subsystem.getProperty("Maximum Output").getValue()}));
#else
    #variable($component.name).Set(output);
#end
#end
#end