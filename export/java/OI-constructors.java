#foreach ($component in $components)
#if ($helper.exportsTo("OI", $component))
        $helper.getConstructor($component)
        $helper.getExtra($component)
#end
#end
