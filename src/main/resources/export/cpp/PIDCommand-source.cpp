#set($command = $helper.getByName($command-name, $robot))
#set($sensor = $command.getProperty("Input").getValue())
#set($subsystem = $command.getProperty("Requires").getValue())
#foreach ($component in $components)
#if ($component.name == $sensor)
        return Robot::#variable($subsystem)->Get#class($component.name)()->PIDGet();
#end
#end
