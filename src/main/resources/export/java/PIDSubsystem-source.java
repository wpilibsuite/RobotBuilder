#set($subsystem = $helper.getByName($subsystem_name, $robot))
#foreach ($component in $components)
#if ($component.name == $subsystem.getProperty("Input").getValue() && $component.getSubsystem().equals($subsystem_name.concat(" ")))
        return #pid($component);
#end
#end