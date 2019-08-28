#set($command = $helper.getByName($command_name, $robot))

#class($command.name)::#class($command.name)(double setpoint) : Command() {
    m_setpoint = setpoint;
