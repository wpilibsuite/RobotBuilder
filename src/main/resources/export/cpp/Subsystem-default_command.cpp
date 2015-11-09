#set($subsystem = $helper.getByName($subsystem-name, $robot))
#set($command = $subsystem.getProperty("Default Command").getValue())
#set($params = $subsystem.getProperty("Default command parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

#if ($command != "None")
        SetDefaultCommand(new #class($command)(#if( $len >= 0 )#foreach($i in [0..$len])#param_set( $params.get($i) ), #end#end#param_set( $params.get($last) )));
#end
