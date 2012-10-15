#set($autonomous = $robot.getProperty("Autonomous Command").getValue())
#if($autonomous != "None")        autonomousCommand = new #class($autonomous)();
#end
