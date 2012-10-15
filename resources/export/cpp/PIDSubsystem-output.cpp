#set($subsystem = $helper.getByName($subsystem-name, $robot))
#set($component = $subsystem.getProperty("Output").getValue())
#set($name = ${helper.getByName($component, $robot).name})
#if($name)	#variable($name)->PIDWrite(output);
#end
