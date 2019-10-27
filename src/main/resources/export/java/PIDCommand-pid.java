/*
Add the following methods to your subsystem that this command is under control of
or
Change the subsystem::getPIDCommand_Input and subsystem::setPIDCommand_Output to methods 
already created in your subsystem

        //This is the PID getter
        public double getPIDCommand_Input(){
                return 0.0; //Insert your feedback device here
        }

        //This is the PID setter
        public void setPIDCommand_Output(double currentValue){
                //motor.set(currentValue);
        }

*/
#set($command = $helper.getByName($command_name, $robot))
        super(new PIDController(${command.getProperty("P").getValue()}, ${command.getProperty("I").getValue()}, ${command.getProperty("D").getValue()}),  subsystem::getPIDCommand_Input, ThisSetpoint , output -> subsystem.setThisPID(output));
        getController().setTolerance(${command.getProperty("Tolerance").getValue()});
        #if(${command.getProperty("Continuous").getValue()})
        getController().enableContinuousInput(${command.getProperty("Minimum Input").getValue()}, ${command.getProperty("Maximum Input").getValue()});
        #end
#if($command.getProperty("Limit Input").getValue())
        //Find new methods
        //getPIDController().setInputRange(${command.getProperty("Minimum Input").getValue()}, ${command.getProperty("Maximum Input").getValue()});
#end
#if($command.getProperty("Limit Output").getValue())
        //Find new methods
        //getPIDController().setOutputRange(${command.getProperty("Minimum Output").getValue()}, ${command.getProperty("Maximum Output").getValue()});
#end
