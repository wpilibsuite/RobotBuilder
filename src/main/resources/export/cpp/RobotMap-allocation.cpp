#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
#type($component)* RobotMap::#variable($component.fullName) = NULL;
#end
#end
