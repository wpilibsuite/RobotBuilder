#set($command = $helper.getByName($command_name, $robot))
#if ($command.getProperty("Requires").getValue() != "None")

        m_#variable($command.getProperty("Requires").getValue()) = subsystem;
        addRequirements(m_#variable($command.getProperty("Requires").getValue()));
#else

        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);    
#end
