#set($command = $helper.getByName($command_name, $robot))
        Robot.#variable(${command.getProperty("Requires").getValue()}).enable();
        Robot.#variable(${command.getProperty("Requires").getValue()}).setSetpoint(m_setpoint);
