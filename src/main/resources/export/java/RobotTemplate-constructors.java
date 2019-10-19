#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
        #constructor($component)

#if ($component.getProperty("Send to SmartDashboard").getValue())
        SmartDashboard.putData(#variable($component.name));
#end
#end
#end
