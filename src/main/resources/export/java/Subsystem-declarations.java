#set($subsystem = $helper.getByName($subsystem_name, $robot))
#foreach ($component in $components)
#if ($component.subsystem == $subsystem.subsystem && $component != $subsystem)
    #declaration($component)

#end
#end
