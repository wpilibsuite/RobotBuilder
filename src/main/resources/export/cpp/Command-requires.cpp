#set($command = $helper.getByName($command_name, $robot))
#set($first = 1)
#if ($command.getProperty("Requires").getValue() != "None")
	#set($cmd1 = ${command.getProperty("Requires").getValue()})
	#set($cmd2 = ${cmd1.toLowerCase()})
	#set($cmd = "m_${cmd2}")
	#if($first)
## #variable(${cmd})(#variable(${cmd2}))
AddRequirements(#variable(${command.getProperty("Requires").getValue()}));
		#set($first = 0)
	#else
		## AddRequirements(#variable(${command.getProperty("Requires").getValue()}));
,#variable(${cmd})(#variable(${cmd2}))
	#end
#end
