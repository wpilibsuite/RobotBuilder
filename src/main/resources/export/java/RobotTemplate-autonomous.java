#set($command = $robot.getProperty("Autonomous Command").getValue())
#set($params = $robot.getProperty("Autonomous command parameters").getValue())

#if( $command != "None" )        autonomousCommand = #command_instantiation( $command $params );
#end
