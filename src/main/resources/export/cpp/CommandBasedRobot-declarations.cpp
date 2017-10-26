#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
	#declaration($component)

#end
#end

#set($command = $robot.getProperty("Autonomous Command").getValue())

#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Autonomous Selection").getValue())
	std::unique_ptr<frc::Command> #class($component.getName());
#end
#end

#if($command != "None")
	std::unique_ptr<frc::Command> #class($command);
#end