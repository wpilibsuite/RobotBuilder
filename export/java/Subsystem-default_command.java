#set($subsystem = $helper.getByName($subsystem-name, $robot))
#if ($subsystem.getProperty("Default Command") != "None")
        setDefaultCommand(new #class($subsystem.getProperty("Default Command"))());
#end
