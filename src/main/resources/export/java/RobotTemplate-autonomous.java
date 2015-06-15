#set($command = $robot.getProperty("Autonomous Command").getValue())
#set($params = $robot.getProperty("Parameters").getValue())

#if(!"None".equals( $command ))        autonomousCommand = #command_instantiation( $command $params );
#end
