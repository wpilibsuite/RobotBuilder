#foreach ($component in $components)
#if ($helper.exportsTo("RobotContainer", $component)
&& "#type($component)" != "" 
&& ("#type($component)" == "SubsystemBase"
     || "#type($component)" == "PIDSubsystem"))
#if ($component.getProperty("Default Command").value != "None")
    m_#variable($component.getName()).setDefaultCommand(new #class($component.getProperty("Default Command").value)(m_#variable($component.getName())));
#else
    //m_#variable($component.getName()).setDefaultCommand(new "Add a Command");
#end
#end
#end

