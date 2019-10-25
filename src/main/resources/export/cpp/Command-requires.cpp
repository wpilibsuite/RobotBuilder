#set($command = $helper.getByName($command_name, $robot))
#if ($command.getProperty("Requires").getValue() != "None")
	Requires(Robot::#variable(${command.getProperty("Requires").getValue()}).get());
#end
