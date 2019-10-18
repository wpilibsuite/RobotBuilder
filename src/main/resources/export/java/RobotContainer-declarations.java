#foreach ($component in $components)
#if ($helper.exportsTo("RobotContainer", $component))
    #declaration($component)

#end
#end