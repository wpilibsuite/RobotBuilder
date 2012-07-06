#foreach ($component in $components)
#if ($helper.exportsTo("CommandBase", $component))
    $helper.getConstructor($component)
#end
#end
