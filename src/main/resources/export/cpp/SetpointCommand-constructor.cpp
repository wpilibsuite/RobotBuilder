#set($command = $helper.getByName($command_name, $robot))

#class($command.name)::#class($command.name)(double setpoint,     #class(${command.getProperty("Requires").getValue()})* m_#variable(${command.getProperty("Requires").getValue().toLowerCase()})) :
m_setpoint(setpoint), m_#variable(${command.getProperty("Requires").getValue().toLowerCase()})(m_#variable(${command.getProperty("Requires").getValue().toLowerCase()})){
SetName("#class($command.name)");
AddRequirements(m_#variable(${command.getProperty("Requires").getValue().toLowerCase()}));