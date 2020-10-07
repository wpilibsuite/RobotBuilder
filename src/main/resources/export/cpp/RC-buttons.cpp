
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
m_#variable($component.name).$component.getProperty("When to Run").getValue().substring(0,1).toUpperCase()$component.getProperty("When to Run").getValue().substring(1)(#class($command.name)(#if($command.getProperty("Requires").getValue() != "None")&m_#required_subsystem($command)#end)#if($component.getProperty("Add Timeout").value == true).WithTimeout($component.getProperty("Timeout").getValue()_s)#end);
#end
#end
#end
#end
