#set($subsystem = $helper.getByName($subsystem_name, $robot))
#set($command = $subsystem.getProperty("Default Command").getValue())
#set($params = $subsystem.getProperty("Default command parameters").getValue())

#if ($command != "None" && $command != "")
        setDefaultCommand(#command_instantiation( $command $params ));
#end
