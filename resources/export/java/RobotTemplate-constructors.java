#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
        #constructor($component)

#end
#end
