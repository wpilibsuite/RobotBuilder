#foreach ($component in $components)
#if ($helper.exportsTo("RobotContainer", $component))
#constructor($component)
#end
#end
