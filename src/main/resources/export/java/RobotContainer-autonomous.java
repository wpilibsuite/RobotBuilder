#set($command = $helper.getByName($robot.getProperty("Autonomous Command").getValue(), $robot))
#set($params = $robot.getProperty("Autonomous command parameters").getValue())

#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Autonomous Selection").getValue())
#if( $component.getProperty("Parameter presets").getValue().isEmpty() )
    m_chooser.addOption("$component.getName()", #new_command_instantiation_nt( $component $params ));
#else
#foreach( $set in $component.getProperty("Parameter presets").getValue() )
    m_chooser.addOption("$component.getName(): $set.getName()", #new_command_instantiation_nt( $component, $set.getParameters() ));
#end
#end
#end
#end
#if($robot.getProperty("Autonomous Command").getValue() != "None")
    m_chooser.setDefaultOption("$command.getName()", #new_command_instantiation_nt( $command $params ));
#end
