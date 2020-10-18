#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
#class($component.name)* m_#variable($component.name);
#end
#end
