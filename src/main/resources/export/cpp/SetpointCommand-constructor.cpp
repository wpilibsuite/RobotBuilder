#set($command = $helper.getByName($command_name, $robot))

#class($command.name)::#class($command.name)(double setpoint,     #class(${command.getProperty("Requires").getValue()})* #variable(${command.getProperty("Requires").getValue().toLowerCase()})) :
m_setpoint(setpoint), #variable(${command.getProperty("Requires").getValue().toLowerCase()})(#variable(${command.getProperty("Requires").getValue().toLowerCase()})){
SetName("#class($command.name)");
AddRequirements(#variable(${command.getProperty("Requires").getValue().toLowerCase()}));