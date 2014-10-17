#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
	#declaration($component)

#end
#end
