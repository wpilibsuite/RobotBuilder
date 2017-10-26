#set($command = $robot.getProperty("Autonomous Command").getValue())

#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Autonomous Selection").getValue())
	chooser.AddObject("$component.getName()", #class($component.getName()).get());
#end
#end

#if($command != "None")
	chooser.AddDefault("$command", #class($command).get());
#end