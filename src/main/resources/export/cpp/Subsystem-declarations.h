#set($subsystem = $helper.getByName($subsystem_name, $robot))
#foreach ($component in $components)
#if ($component.subsystem == $subsystem.subsystem && $component != $subsystem)
	std::shared_ptr<#type($component)> #variable($component.name);
#end
#end
