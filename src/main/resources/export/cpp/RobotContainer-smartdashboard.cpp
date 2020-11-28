    // Smartdashboard Subsystems
#foreach ($component in $components)
#if ($helper.exportsTo("RobotContainer", $component))
#if ($component.getProperty("Send to SmartDashboard").getValue())
    frc::SmartDashboard::PutData(&m_#variable($component.name));
#end
#end
#end

${Collections.reverse($components)}
    // SmartDashboard Buttons
#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Button on SmartDashboard").getValue())
#if( $component.getProperty("Parameter presets").getValue().isEmpty())
    frc::SmartDashboard::PutData("$component.getName()", new #new_command_instantiation( $component, $component, $component.getProperty("Parameter presets").getValue()));
#else
#foreach( $set in $component.getProperty("Parameter presets").getValue() )
    frc::SmartDashboard::PutData("$component.getName(): $set.getName()", new #new_command_instantiation( $component, $component, $set.getParameters() ));
#end
#end
#end
#end
