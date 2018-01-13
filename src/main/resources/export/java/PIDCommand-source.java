#set($command = $helper.getByName($command-name, $robot))
#set($component = $command.getProperty("Input").getValue())
#set($name = ${helper.getByName($component, $robot).fullName})
#set($subsystem = $command.getProperty("Requires").getValue())
#if($name)        return Robot.#variable($subsystem).get#class($name)().pidGet();
#end
