#set($command = $helper.getByName($command_name, $robot))
    m_#variable(${command.getProperty("Requires").getValue().toLowerCase()})->Enable();
    m_#variable(${command.getProperty("Requires").getValue().toLowerCase()})->SetSetpoint(m_setpoint);
