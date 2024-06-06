// Create some buttons
#foreach( $component in $components )
#if ($helper.exportsTo("OI", $component)
    && "#base_type($component)" != "Joystick" 
    && ("#constructor($component)" != "" || "#extra($component)" != ""))
        #constructor($component)
        ${Collections.reverse($commands)}
        #foreach ($command in $commands)
                #if($command.name == $component.getProperty("Command").value)
                        #set($params = $component.getProperty("Parameters").getValue())
                        #variable($component.name).$component.getProperty("When to Run").getValue()(#new_command_instantiation($component,$command,$params).withInterruptBehavior(InterruptionBehavior.k$component.getProperty("Interruptible").getValue()));
                        #if ($component.getProperty("Send to SmartDashboard").getValue())
                        SmartDashboard.putData("$component.getName()",#new_command_instantiation($component,$command,$params));
                        #end
                        
                #end
        #end
#end
#end

