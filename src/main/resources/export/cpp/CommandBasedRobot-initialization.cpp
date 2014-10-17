#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
#class($component.name)* Robot::#variable($component.name) = 0;
#end
#end
OI* Robot::oi = 0;
