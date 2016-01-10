#set($subsystem = $helper.getByName($subsystem-name, $robot))
#set($command = $subsystem.getProperty("Default Command").getValue())
#set($params = $subsystem.getProperty("Default command parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

#if ($command != "None")
        SetDefaultCommand(#command_instantiation( $command $params ));
#end
