#set($command = $helper.getByName($command-name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#foreach($param in $params)
        m_$param.getName() = $param.getName();
#end