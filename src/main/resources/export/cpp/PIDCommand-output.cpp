#set($command = $helper.getByName($command_name, $robot))
#set($actuator = $command.getProperty("Output").getValue())
#set($subsystem = $command.getProperty("Requires").getValue())
#foreach ($component in $components)
#if ($component.name == $actuator)
    Robot::#variable($subsystem)->Get#class($component.name)()->PIDWrite(output);
#end
#end