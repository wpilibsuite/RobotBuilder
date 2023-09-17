#foreach ($component in $components)
#if ($helper.exportsTo("RobotContainer", $component)
&& "#type($component)" != "" 
&& ("#type($component)" == "SubsystemBase"
     || "#type($component)" == "PIDSubsystem"))
#foreach ($command in $commands)
#if($command.name == $component.getProperty("Default Command").value)
#if ($component.getProperty("Default Command").value != "None")
#set($params = $component.getProperty("Default command parameters").getValue())
    m_#variable($component.getName()).setDefaultCommand(#new_command_instantiation($component,$command,$params));
#else
    //m_#variable($component.getName()).setDefaultCommand(new "Add a Command");
#end
#end
#end
#end
#end

