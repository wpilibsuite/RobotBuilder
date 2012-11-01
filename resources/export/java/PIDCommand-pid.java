#set($command = $helper.getByName($command-name, $robot))
        super("#class($command-name)", ${command.getProperty("P").getValue()}, ${command.getProperty("I").getValue()}, ${command.getProperty("D").getValue()}, ${command.getProperty("Period").getValue()});
        getPIDController().setContinuous(${command.getProperty("Continuous").getValue()});
        getPIDController().setAbsoluteTolerance(${command.getProperty("Tolerance").getValue()});
#if($command.getProperty("Limit Input").getValue())
        getPIDController().setInputRange(${command.getProperty("Minimum Input").getValue()}, ${command.getProperty("Maximum Input").getValue()});
#end
#if($command.getProperty("Limit Output").getValue())
        getPIDController().setOutputRange(${command.getProperty("Minimum Output").getValue()}, ${command.getProperty("Maximum Output").getValue()});
#end
