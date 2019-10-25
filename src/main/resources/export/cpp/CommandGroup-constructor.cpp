#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

#if( $params.size() > 0 )
#class($command.name)::#class($command.name)(#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_cpp($params.get($i)), #end#end#if( $last >= 0 )#param_declaration_cpp($params.get($last))#end): CommandGroup() {
#else
#class($command.name)::#class($command.name)() {
#end
#if ( $command.getProperty("Run When Disabled").getValue() )
    SetRunWhenDisabled(true);
#end
