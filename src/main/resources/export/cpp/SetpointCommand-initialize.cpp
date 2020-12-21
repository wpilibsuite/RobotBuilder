#set($command = $helper.getByName($command_name, $robot))
    #variable(${command.getProperty("Requires").getValue().toLowerCase()})->Enable();
    #variable(${command.getProperty("Requires").getValue().toLowerCase()})->SetSetpoint(m_setpoint);
