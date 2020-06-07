#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

#if( $params.size() != 0 )
#foreach( $param in $params )
    #param_type( $param ) m_${param.getName()};
## generates Foo m_foo;
#end
#end

#set($command = $helper.getByName($command_name, $robot))
#set($first = 1)
#if ($command.getProperty("Requires").getValue() != "None")
	#set($cmd1 = ${command.getProperty("Requires").getValue()})
	#set($cmd2 = ${cmd1.toLowerCase()})
	#set($cmd = "m_${cmd2}")
	#if($first)
#class(${command.getProperty("Requires").getValue()})* #variable(${command.getProperty("Requires").getValue().toLowerCase()});
		#set($first = 0)
	#else
		## AddRequirements(Robot::#variable(${command.getProperty("Requires").getValue()}));
,#class(${command.getProperty("Requires").getValue()})* #variable(${command.getProperty("Requires").getValue().toLowerCase()});
#end
#end

