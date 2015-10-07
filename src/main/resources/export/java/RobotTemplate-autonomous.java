#set($command = $robot.getProperty("Autonomous Command").getValue())
#set($params = $robot.getProperty("Parameters").getValue())

#if( $command != "None" )        autonomousCommand = #command_instantiation( $command $params );
#end
