#set($command = $helper.getByName($command-name, $robot))
        Robot.#variable(${command.getProperty("Requires").getValue()}).enable();
        Robot.#variable(${command.getProperty("Requires").getValue()}).setSetpoint(${command.getProperty("Setpoint").getValue()});
