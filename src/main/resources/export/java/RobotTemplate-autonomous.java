#set($command = $robot.getProperty("Autonomous Command").getValue())

#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Autonomous Selection").getValue())
        chooser.addObject("$component.getName()", new #class($component.getName())());
#end
#end

#if($command != "None")
        chooser.addDefault("$command", new #class($command)());
#end