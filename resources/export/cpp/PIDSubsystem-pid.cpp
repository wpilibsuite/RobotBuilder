#class($subsystem.name)::#class($subsystem.name)() : PIDSubsystem("#class($subsystem.name)", ${subsystem.getProperty("P").getValue()}, ${subsystem.getProperty("I").getValue()}, ${subsystem.getProperty("D").getValue()}) {
#set($subsystem = $helper.getByName($subsystem-name, $robot))
        getPIDController()->SetContinuous(${subsystem.getProperty("Continuous").getValue()});
#if($subsystem.getProperty("Limit Input").getValue())
        getPIDController()->SetInputRange(${subsystem.getProperty("Minimum Input").getValue()}, ${subsystem.getProperty("Maximum Input").getValue()});
#end
#if($subsystem.getProperty("Limit Output").getValue())
        getPIDController()->SetOutputRange(${subsystem.getProperty("Minimum Output").getValue()}, ${subsystem.getProperty("Maximum Output").getValue()});
#end
