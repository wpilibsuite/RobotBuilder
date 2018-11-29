#set($subsystem = $helper.getByName($subsystem-name, $robot))
#foreach ($component in $components)
#if ($component.name == $subsystem.getProperty("Output").getValue() && $component.getSubsystem().equals($subsystem-name.concat(" ")))
        #variable($component.name).pidWrite(output);
#end
#end