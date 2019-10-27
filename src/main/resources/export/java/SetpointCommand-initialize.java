#set($command = $helper.getByName($command_name, $robot))
        m_#variable($command.getProperty("Requires").getValue()).enable();
        m_#variable($command.getProperty("Requires").getValue()).setSetpoint(m_setpoint);
