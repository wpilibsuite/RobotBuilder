#set($autonomous = $robot.getProperty("Autonomous Command").getValue())
#if($autonomous != "None")	autonomousCommand.reset(#command_instantiation( $autonomous, $robot.getProperty("Parameters").getValue() ));
#end
