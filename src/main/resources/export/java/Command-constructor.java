#set($command = $helper.getByName($command-name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

#foreach($param in $params)
    private $param.getType() m_$param.getName();
#end ## variable declaration

#if( $params.size() > 0 )
    public #class($command.name)(#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_java($params.get($i)), #end#end#param_declaration_java($params.get($last))) {
#else
    public #class($command.name)() {
#end
        super();
#foreach($param in $params)
        this.m_$param.getName() = $param.getName();
#end