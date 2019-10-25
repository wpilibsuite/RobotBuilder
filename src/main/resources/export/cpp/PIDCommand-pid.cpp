#set($command = $helper.getByName($command_name, $robot))
        GetPIDController()->SetContinuous(${command.getProperty("Continuous").getValue()});
        GetPIDController()->SetAbsoluteTolerance(${command.getProperty("Tolerance").getValue()});
#if($command.getProperty("Limit Input").getValue())
        GetPIDController()->SetInputRange(${command.getProperty("Minimum Input").getValue()}, ${command.getProperty("Maximum Input").getValue()});
#end
#if($command.getProperty("Limit Output").getValue())
        GetPIDController()->SetOutputRange(${command.getProperty("Minimum Output").getValue()}, ${command.getProperty("Maximum Output").getValue()});
#end
