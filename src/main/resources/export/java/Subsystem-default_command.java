#set($subsystem = $helper.getByName($subsystem-name, $robot))
#set($command = $subsystem.getProperty("Default Command").getValue())
#set($params = $subsystem.getProperty("Parameters").getValue())

#if ($command != "None" && $command != "")
        setDefaultCommand(#command_instantiation( $command $params ));
#end
