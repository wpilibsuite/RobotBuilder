#foreach ($component in $components)
#if ($helper.exportsTo("OI", $component))
#if ("#declaration($component)" != "")
	#declaration($component)

#end
#end
#end
