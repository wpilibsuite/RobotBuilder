#foreach ($component in $components)
#if ($helper.exportsTo("OI", $component))
    $helper.getDeclaration($component)
#end
#end
