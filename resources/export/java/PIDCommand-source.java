#set($command = $helper.getByName($command-name, $robot))
#set($component = $command.getProperty("Input").getValue())
#set($name = ${helper.getByName($component, $robot).fullName})
#if($name)        return RobotMap.#variable($name).pidGet();
#end
