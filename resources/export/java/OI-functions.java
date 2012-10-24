#foreach ($component in $components)
#if ($helper.exportsTo("OI", $component)
     && "#function($component)" != "")
    #function($component)


#end
#end
