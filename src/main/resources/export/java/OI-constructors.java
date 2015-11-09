${Collections.reverse($components)}
#foreach( $component in $components )
#if ($helper.exportsTo("OI", $component)
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
#if( $component.getProperty("Parameter presets").getValue().size() == 0 )
#if( $component.getProperty("Default command parameters").getValue().size() == 0 )
## "Use the default constructor only if one's going to be generated"
        SmartDashboard.putData("$component.getName()", new #class($component.getName())());
#end
#else
#foreach( $set in $component.getProperty("Parameter presets").getValue() )
        SmartDashboard.putData("$component.getName(): $set.getName()", #command_instantiation( $component.getName(), $set.getParameters() ));
#end
#end
#end
#end
