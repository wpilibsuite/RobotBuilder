#set($autonomous = $robot.getProperty("Autonomous Command").getValue())
#if($autonomous != "None")    autonomousCommand = #command_instantiation( $autonomous, $robot.getProperty("Parameters").getValue() );
#end
