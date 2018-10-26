#set($subsystem = $helper.getByName($subsystem-name, $robot))
#foreach ($component in $components)
#if ($component.subsystem == $subsystem.subsystem && $component != $subsystem)
     #constructor($component)
#end
#end
