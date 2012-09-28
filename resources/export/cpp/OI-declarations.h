#foreach ($component in $components)
#if ($helper.exportsTo("OI", $component))
	#declaration($component)

#end
#end
