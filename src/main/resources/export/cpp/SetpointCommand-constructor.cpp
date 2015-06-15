#set($command = $helper.getByName($command-name, $robot))

#class($command.name)::#class($command.name)(double setpoint) : Command() {
    m_setpoint = setpoint;
