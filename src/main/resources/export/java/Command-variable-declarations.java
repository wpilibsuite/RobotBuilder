#if ($command.getProperty("Requires").getValue() != "None")
        private final #class($command.getProperty("Requires").getValue()) m_#variable($command.getProperty("Requires").getValue());
#end
#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#foreach($param in $params)
    private $param.getType() m_$param.getName();
#end ## variable declaration

#if($command.getProperty("DoubleSupplier 1").getValue() != "None")private DoubleSupplier m_doublesupplier1;#end
#if($command.getProperty("DoubleSupplier 2").getValue() != "None")private DoubleSupplier m_doublesupplier2;#end