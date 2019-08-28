#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)
#if( $params.size() > 0 )
	#class($command.name)(#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_cpp($params.get($i)), #end#end#param_declaration_cpp($params.get($last)));
#else
	#class($command.name)();
#end
