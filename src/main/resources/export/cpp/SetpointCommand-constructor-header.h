#set($command = $helper.getByName($command_name, $robot))

#class($command.name)(double setpoint, #class(${command.getProperty("Requires").getValue()})* m_#variable(${command.getProperty("Requires").getValue().toLowerCase()}));


