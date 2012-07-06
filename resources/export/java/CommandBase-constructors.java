#foreach ($component in $components)
#if ($helper.exportsTo("CommandBase", $component))
    #constructor($component)

#end
#end
