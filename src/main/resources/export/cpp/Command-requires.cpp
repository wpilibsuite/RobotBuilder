#set($command = $helper.getByName($command_name, $robot))
#if ($command.getProperty("Requires").getValue() != "None")
	AddRequirements(Robot::#variable(${command.getProperty("Requires").getValue()}));
#end
