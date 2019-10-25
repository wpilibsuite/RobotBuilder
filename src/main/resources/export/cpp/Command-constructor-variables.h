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
