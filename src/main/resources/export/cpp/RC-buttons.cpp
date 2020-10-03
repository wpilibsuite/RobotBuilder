
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
m_#variable($component.name).$component.getProperty("When to Run").getValue().substring(0,1).toUpperCase()$component.getProperty("When to Run").getValue().substring(1)(#class($command.name)(&m_#required_subsystem($command))#if($command.getProperty("Add Timeout").value == true).withTimeout($component.getProperty("Timeout").value)#end);#else#if($command.getProperty("Add Timeout").value == true).withTimeout($command.getProperty("Timeout").value));#end#end
#end
#end
#end