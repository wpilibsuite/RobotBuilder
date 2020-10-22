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
                        #variable($component.name).$component.getProperty("When to Run").getValue()(#new_command_instantiation($component,$command,$params),$component.getProperty("Interruptible").getValue());
                        SmartDashboard.putData("$component.getName()",#new_command_instantiation($component,$command,$params));
                        
                #end
        #end
        
#end
#end

${Collections.reverse($components)}
        // SmartDashboard Buttons
#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Button on SmartDashboard").getValue())
#if( $component.getProperty("Parameter presets").getValue().isEmpty() )
<<<<<<< HEAD
        SmartDashboard.putData("$component.getName()", #new_command_instantiation_nt($component, $component.getProperty("Parameter presets").getValue()));
#else
#foreach( $set in $component.getProperty("Parameter presets").getValue() )
        SmartDashboard.putData("$component.getName(): $set.getName()", #new_command_instantiation_nt($component, $set.getParameters()));
=======
        SmartDashboard.putData("$component.getName()", #command_instantiation_SmartDashoard_BTN_No_Par($component.getName(), $component));
#else
#foreach( $set in $component.getProperty("Parameter presets").getValue() )
        SmartDashboard.putData("$component.getName(): $set.getName()", #command_instantiation_SmartDashoard_BTN($component.getName(), $set.getParameters(), $component));
>>>>>>> 7c08fdaf10d5d1ea241bf225524b70e0bed7dd74
#end
#end
#end
#end
