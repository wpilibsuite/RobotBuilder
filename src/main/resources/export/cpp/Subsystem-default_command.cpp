#set($subsystem = $helper.getByName($subsystem-name, $robot))
#if ($subsystem.getProperty("Default Command").getValue() != "None")
	SetDefaultCommand(new #class($subsystem.getProperty("Default Command").getValue())());
#end
