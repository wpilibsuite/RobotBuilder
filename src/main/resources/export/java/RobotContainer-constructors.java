// Create some buttons
#foreach( $component in $components )
#if ($helper.exportsTo("OI", $component)
    && "#type($component)" != "Joystick" 
    && ("#constructor($component)" != "" || "#extra($component)" != ""))
        #constructor($component)
        ${Collections.reverse($commands)}
        #foreach ($command in $commands)
                #if($command.name == $component.getProperty("Command").value)
                        #set($params = $component.getProperty("Parameters").getValue())
                        #variable($component.name).$component.getProperty("When to Run").getValue()(#new_command_instantiation($component,$command,$params)#if($component.getProperty("Add Timeout").value == true).withTimeout($component.getProperty("Timeout").value));#else#if($command.getProperty("Add Timeout").value == true).withTimeout($command.getProperty("Timeout").value));#else);#end#end
                        
                #end
        #end
        
#end
#end

        // SmartDashboard Buttons
#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Button on SmartDashboard").getValue())
        #if( $component.getProperty("Parameter presets").getValue().isEmpty() )
                //No Parameters
                #if ($component.getProperty("Requires").getValue() != "None")
                        //Does Require Subsystem
                        SmartDashboard.putData("$component.getName()", new #class($component.getName())(m_#required_subsystem($component))#if($component.getProperty("Add Timeout").value == true).withTimeout($component.getProperty("Timeout").value)#end);
                #else
                        //Does Not Require Subsystem
                        SmartDashboard.putData("$component.getName()", new #class($component.getName())()#if($component.getProperty("Add Timeout").value == true).withTimeout($component.getProperty("Timeout").value)#end);
                #end
        #else
                //Has Parameters
                #if ($component.getProperty("Requires").getValue() != "None")
                        //Does Require Subsystem
                        #foreach( $set in $component.getProperty("Parameter presets").getValue() )
                                SmartDashboard.putData("$component.getName(): $set.getName()", new #class($component.getName())(#command_params($set.getParameters()), m_#required_subsystem($component))#if($component.getProperty("Add Timeout").value == true).withTimeout($component.getProperty("Timeout").value)#end);
                        #end
                #else
                        //Does Not Require Subsystem
                        #foreach( $set in $component.getProperty("Parameter presets").getValue() )
                                SmartDashboard.putData("$component.getName(): $set.getName()", new #class($component.getName())(#command_params($set.getParameters()))#if($component.getProperty("Add Timeout").value == true).withTimeout($component.getProperty("Timeout").value)#end);
                        #end
                #end
        #end
#end
#end

