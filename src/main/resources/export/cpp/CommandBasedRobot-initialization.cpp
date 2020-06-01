#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
std::shared_ptr<#class($component.name)> Robot::#variable($component.name);
#end
#end
