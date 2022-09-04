
#foreach( $component in $components )
#if ($helper.exportsTo("OI", $component) && ("#type($component)" == "JoystickButton") && ("#constructor($component)" != "" || "#extra($component)" != ""))
#foreach ($command in $commands)
#if($command.name == $component.getProperty("Command").value)
#constructor($component)

#end
#end
#end
#end

#foreach( $component in $components )
#if ($helper.exportsTo("OI", $component) && ("#type($component)" != "Joystick") && ("#constructor($component)" != "" || "#extra($component)" != ""))
#foreach ($command in $commands)
#if($command.name == $component.getProperty("Command").value)
#set($params = $component.getProperty("Parameters").getValue())
#variable($component.name).$component.getProperty("When to Run").getValue().substring(0,1).toUpperCase()$component.getProperty("When to Run").getValue().substring(1)(#new_command_instantiation($component, $command, $params)#if($component.getProperty("Add Timeout").value == true).WithTimeout($component.getProperty("Timeout").getValue()_s)#end, $component.getProperty("Interruptible").getValue());
#if ($component.getProperty("Send to SmartDashboard").getValue())
    frc::SmartDashboard::PutData("#variable($component.name)", new #new_command_instantiation($component, $command, $params)#if($component.getProperty("Add Timeout").value == true).WithTimeout($component.getProperty("Timeout").getValue()_s)#end);
#end

#end
#end
#end
#end
