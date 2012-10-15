#set($subsystem = $helper.getByName($subsystem-name, $robot))
#if ($subsystem.getProperty("Default Command").getValue() != "None")
        setDefaultCommand(new #class($subsystem.getProperty("Default Command").getValue())());
#end
