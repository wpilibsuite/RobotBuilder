#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
#type($component)* RobotMap::#constant($component.fullName) = 0;
#end
#end
