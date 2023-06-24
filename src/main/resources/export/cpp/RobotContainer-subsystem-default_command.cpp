#foreach ($component in $components)
#if ("#type($component)" == "frc2::Subsystem" || "#type($component)" == "frc2::PIDSubsystem")
#foreach ($command in $commands)
#if($command.name == $component.getProperty("Default Command").value)
#set($params = $component.getProperty("Default command parameters").getValue())
#if ($command != "None")
        #required_subsystem($command).SetDefaultCommand(#new_command_instantiation($component, $command, $params));
#end
#end
#end
#end
#end