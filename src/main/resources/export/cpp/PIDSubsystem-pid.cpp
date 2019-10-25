#set($subsystem = $helper.getByName($subsystem_name, $robot))
#class($subsystem.name)::#class($subsystem.name)() : PIDSubsystem("#class($subsystem.name)", ${subsystem.getProperty("P").getValue()}, ${subsystem.getProperty("I").getValue()}, ${subsystem.getProperty("D").getValue()}) {
    SetAbsoluteTolerance(${subsystem.getProperty("Tolerance").getValue()});
    GetPIDController()->SetContinuous(${subsystem.getProperty("Continuous").getValue()});
    GetPIDController()->SetName("$subsystem_name", "PIDSubsystem Controller");
    AddChild(GetPIDController());
#if($subsystem.getProperty("Limit Input").getValue())
    GetPIDController()->SetInputRange(${subsystem.getProperty("Minimum Input").getValue()}, ${subsystem.getProperty("Maximum Input").getValue()});
#end
#if($subsystem.getProperty("Limit Output").getValue())
    GetPIDController()->SetOutputRange(${subsystem.getProperty("Minimum Output").getValue()}, ${subsystem.getProperty("Maximum Output").getValue()});
#end
