#set($subsystem = $helper.getByName($subsystem-name, $robot))
#set($input = $subsystem.getProperty("Input").getValue())
#set($component = ${helper.getByName($input, $robot)})
#if($component)        return #pid($component);
#end
