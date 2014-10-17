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
        SmartDashboard.putData("$component.getName()", new #class($component.getName())());

#end
#end
