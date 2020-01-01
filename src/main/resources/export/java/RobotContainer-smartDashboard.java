#foreach ($component in $components)
#if ($helper.exportsTo("RobotContainer", $component))
#if ($component.getProperty("Send to SmartDashboard").getValue())
SmartDashboard.putData(m_#variable($component.name));
#end
#end
#end
