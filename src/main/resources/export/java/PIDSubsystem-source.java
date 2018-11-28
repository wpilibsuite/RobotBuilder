#set($subsystem = $helper.getByName($subsystem-name, $robot))
#foreach ($component in $components)
#if ($component.name == $subsystem.getProperty("Input").getValue())
    return #pid($component);
#end
#end