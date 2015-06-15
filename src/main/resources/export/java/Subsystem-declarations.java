#set($subsystem = $helper.getByName($subsystem-name, $robot))
#foreach ($component in $components)
#if ($component.subsystem == $subsystem.subsystem && $component != $subsystem)
    private final #type($component) #variable($component.name) = RobotMap.#variable($component.fullName);
#end
#end
