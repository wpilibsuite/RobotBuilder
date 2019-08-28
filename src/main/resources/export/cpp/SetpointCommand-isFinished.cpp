#set($command = $helper.getByName($command_name, $robot))
    return Robot::#variable(${command.getProperty("Requires").getValue()})->OnTarget();
