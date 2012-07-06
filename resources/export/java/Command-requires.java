#set($command = $helper.getByName($command-name, $robot))
#if ($command.getProperty("Requires") != "None")
    requires(#variable(${command.getProperty("Requires").getValue()}));
#end
