#set($command = $helper.getByName($robot.getProperty("Autonomous Command").getValue(), $robot))
#set($params = $robot.getProperty("Autonomous command parameters").getValue())

#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Autonomous Selection").getValue())
#if( $component.getProperty("Parameter presets").getValue().isEmpty())
    m_chooser.AddOption("$component.getName()", new #new_command_instantiation( $component, $component, $component.getProperty("Parameter presets").getValue()));
#else
#foreach( $set in $component.getProperty("Parameter presets").getValue() )
    m_chooser.AddOption("$component.getName(): $set.getName()", new #new_command_instantiation( $component, $component, $set.getParameters() ));
#end
#end
#end
#end

    m_chooser.SetDefaultOption("$command.getName()", new #new_command_instantiation( $command, $command, $params ));
