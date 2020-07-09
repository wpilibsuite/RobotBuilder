#set($subsystem = $helper.getByName($subsystem_name, $robot))
#foreach ($component in $components)
    #if ($component.name == $subsystem.getProperty("Input").getValue() && $component.getSubsystem().equals($subsystem_name.concat(" ")))
        #if($subsystem.getProperty("Limit Input").getValue() && !${subsystem.getProperty("Continuous").getValue()} )
        return MathUtil.clamp(#pid($component), ${subsystem.getProperty("Minimum Input").getValue()}, ${subsystem.getProperty("Maximum Input").getValue()});
        #else
        return #pid($component);
        #end
    #end
#end