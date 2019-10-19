// Create some buttons
${Collections.reverse($components)}
#foreach( $component in $components )
#if ($helper.exportsTo("OI", $component)
    && "#type($component)" != "Joystick" 
    && ("#constructor($component)" != "" || "#extra($component)" != ""))
        #constructor($component)

        #extra($component)

#end
#end

${Collections.reverse($components)}
        // SmartDashboard Buttons
#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Button on SmartDashboard").getValue())
#if( $component.getProperty("Parameter presets").getValue().isEmpty() )
        SmartDashboard.putData("$component.getName()", new #class($component.getName())(m_#variable($component.getProperty("Requires").getValue())));
#else
#foreach( $set in $component.getProperty("Parameter presets").getValue() )
        SmartDashboard.putData("$component.getName(): $set.getName()", #command_instantiation( $component.getName(), $set.getParameters() ));
#end
#end
#end
#end

