#set($subsystem = $helper.getByName($subsystem_name, $robot))
#foreach ($component in $components)
#if ($component.name == $subsystem.getProperty("Output").getValue())
    #variable($component.name)->PIDWrite(output);
#end
#end