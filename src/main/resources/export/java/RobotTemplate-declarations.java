#foreach ($component in $components)
#if ($helper.exportsTo("Robot", $component))
    #declaration($component)
#end
#end
