#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
#type($component)* RobotMap::#constant($component.fullName) = NULL;
#end
#end
