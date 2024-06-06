#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
#class($component.name)* #variable($component.name);
#end
#end
