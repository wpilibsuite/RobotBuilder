#set($command = $helper.getByName($command_name, $robot))

#class($command.name)(double setpoint, #class(${command.getProperty("Requires").getValue()})* #variable(${command.getProperty("Requires").getValue().toLowerCase()}));


