#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
std::shared_ptr<frc::#type($component)> RobotMap::#variable($component.fullName);
#end
#end
