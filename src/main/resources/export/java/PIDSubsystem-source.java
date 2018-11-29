#set($subsystem = $helper.getByName($subsystem-name, $robot))
#foreach ($component in $components)
#if ($component.name == $subsystem.getProperty("Input").getValue() && $component.getSubsystem().equals($subsystem-name.concat(" ")))
        return #pid($component);
#end
#end