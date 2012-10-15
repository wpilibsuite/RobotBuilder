#set($command = $helper.getByName($command-name, $robot))
#set($component = $command.getProperty("Output").getValue())
#set($name = ${helper.getByName($component, $robot).fullName})
#if($name)        RobotMap.#constant($name).pidWrite(output);
#end
