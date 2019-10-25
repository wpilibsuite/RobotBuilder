#set($command = $helper.getByName($command_name, $robot))
#set($component = $command.getProperty("Output").getValue())
#set($name = ${helper.getByName($component, $robot).fullName})
#set($subsystem = $command.getProperty("Requires").getValue())
    #if($name)        return Robot.#variable($subsystem).get#class($name)().pidWrite(output);
#end
