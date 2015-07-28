#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
std::shared_ptr<#class($component.name)> Robot::#variable($component.name);
#end
#end
std::unique_ptr<OI> Robot::oi;
