#set($command = $helper.getByName($command_name, $robot))
        return m_#variable(${command.getProperty("Requires").getValue()}).getController().atSetpoint();
