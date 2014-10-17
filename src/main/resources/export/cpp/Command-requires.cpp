#set($command = $helper.getByName($command-name, $robot))
#if ($command.getProperty("Requires").getValue() != "None")
	Requires(Robot::#variable(${command.getProperty("Requires").getValue()}));
#end
