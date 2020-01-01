#set($subsystem = $helper.getByName($subsystem_name, $robot))
        super(new PIDController( kP, kI, kD));
        getController().setTolerance(${subsystem.getProperty("Tolerance").getValue()});
        #if(${subsystem.getProperty("Continuous").getValue()})
        getController().enableContinuousInput(${subsystem.getProperty("Minimum Input").getValue()}, ${subsystem.getProperty("Maximum Input").getValue()});
        #end
#if($subsystem.getProperty("Limit Input").getValue() && !${subsystem.getProperty("Continuous").getValue()} )
        //Find New methods
        //getController().setInputRange(${subsystem.getProperty("Minimum Input").getValue()}, ${subsystem.getProperty("Maximum Input").getValue()});
#end
#if($subsystem.getProperty("Limit Output").getValue())
        //Find New methods        
        //getController().setOutputRange(${subsystem.getProperty("Minimum Output").getValue()}, ${subsystem.getProperty("Maximum Output").getValue()});
#end
