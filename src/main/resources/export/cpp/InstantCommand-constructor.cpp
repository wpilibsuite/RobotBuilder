#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

#if( $params.size() > 0 )
#class($command.name)::#class($command.name)(#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_cpp($params.get($i)), #end#end#if( $last >= 0 )#param_declaration_cpp($params.get($last))#end): InstantCommand() {
#else
#class($command.name)::#class($command.name)(#class(${command.getProperty("Requires").getValue()})* #variable(${command.getProperty("Requires").getValue().toLowerCase()})): InstantCommand() {
#end
    #foreach($param in $params)
m_$param.getName() = $param.getName();
    #end
    // Use AddRequirements() here to declare subsystem dependencies
    // eg. AddRequirements(Robot::chassis.get());
    SetName("#class($command.name)");
    #if  (${command.getProperty("Requires").getValue()} != "None")
    AddRequirements(#variable(${command.getProperty("Requires").getValue().toLowerCase()}));
    #end