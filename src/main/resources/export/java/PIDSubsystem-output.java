#set($subsystem = $helper.getByName($subsystem_name, $robot))
#foreach ($component in $components)
#if ($component.name == $subsystem.getProperty("Output").getValue() && $component.getSubsystem().equals($subsystem_name.concat(" ")))
        #variable($component.name).pidWrite(output);
#end
#end