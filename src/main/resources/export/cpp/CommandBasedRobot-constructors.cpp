#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
    Robot::#variable($component.name) = std::make_unique<#class($component.name)>
#end
#end

