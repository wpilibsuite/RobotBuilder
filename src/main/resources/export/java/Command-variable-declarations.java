#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#foreach($param in $params)
    private $param.getType() m_$param.getName();
#end ## variable declaration