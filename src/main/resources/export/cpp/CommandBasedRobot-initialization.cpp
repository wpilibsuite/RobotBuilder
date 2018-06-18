std::unique_ptr<OI> Robot::oi;
#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
std::unique_ptr<#class($component.name)> Robot::#variable($component.name);
#end
#end

