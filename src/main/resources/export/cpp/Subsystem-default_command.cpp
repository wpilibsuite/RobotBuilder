#foreach ($component in $components)
#if ("#type($component)" == "frc2::SubsystemBase" || "#type($component)" == "frc2::PIDSubsystem")
#foreach ($command in $commands)
#if($command.name == $component.getProperty("Default Command").value)
#set($len = $params.size() - 2)
#set($last = $len + 1)

#if ($command != "None")
        m_#required_subsystem($command)->SetDefaultCommand(#class($command.name)(m_#required_subsystem($command)));
#end
#end
#end
#end
#end