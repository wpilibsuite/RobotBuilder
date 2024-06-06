#set($command = $helper.getByName($command_name, $robot))
    return #variable(${command.getProperty("Requires").getValue().toLowerCase()})->GetController().AtSetpoint();
