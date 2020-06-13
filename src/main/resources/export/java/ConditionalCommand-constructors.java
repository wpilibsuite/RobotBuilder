#set($command = $helper.getByName($command_name, $robot))
#set($onTrue = $helper.getByName($command.getProperty("On True Command").getValue(), $robot))
#set($onFalse = $helper.getByName($command.getProperty("On False Command").getValue(), $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

#if ($command.getProperty("Requires").getValue() != "None")

#if( $params.size() > 0 )
    public #class($command.name)(#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_java($params.get($i)), #end#end#param_declaration_java($params.get($last)), #class($command.getProperty("Requires").getValue()) subsystem) {
#else
    public #class($command.name)(#class($command.getProperty("Requires").getValue()) subsystem) {

#end
#else
#if( $params.size() > 0 )
    public #class($command.name)(#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_java($params.get($i)), #end#end#param_declaration_java($params.get($last))) {

#else
    public #class($command.name)() {
#end
#end
      super(#new_command_instantiation_cc($onTrue), #new_command_instantiation_cc($onFalse), () ->