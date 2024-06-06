#set($subsystem = $helper.getByName($subsystem_name, $robot))
        super(new PIDController(kP, kI, kD));
        getController().setTolerance(${subsystem.getProperty("Tolerance").getValue()});
        #if(${subsystem.getProperty("Continuous").getValue()})
        getController().enableContinuousInput(${subsystem.getProperty("Minimum Input").getValue()}, ${subsystem.getProperty("Maximum Input").getValue()});
        #end
