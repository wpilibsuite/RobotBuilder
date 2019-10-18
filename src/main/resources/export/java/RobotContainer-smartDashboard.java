#foreach ($component in $components)
#if ($helper.exportsTo("RobotContainer", $component))
#if ($component.getProperty("Send to SmartDashboard").getValue())
SmartDashboard.putData(#variable($component.name));
#end
#end
#end
