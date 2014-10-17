#set($command = $helper.getByName($command-name, $robot))
#if ($command.getProperty("Requires").getValue() != "None")
        requires(Robot.#variable(${command.getProperty("Requires").getValue()}));
#end
