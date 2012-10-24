#foreach ($component in $components)
#if ($helper.exportsTo("OI", $component)
     && "#type($component)" != "" )
    #declaration($component)

#end
#end
