#set($subsystem = $helper.getByName($subsystem-name, $robot))
#foreach ($component in $components)
#if ($component.subsystem == $subsystem.subsystem && $component != $subsystem)
    ${helper.classOf($component)} #variable($component.name) = RobotMap.#constant($component.fullName);
#end
#end
