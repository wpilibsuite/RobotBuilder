#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)
#macro( klass $cmd )#if( "#type($cmd)" == "" )Command#else#type($cmd)#end#end

#if( $params.size() > 0 )
#class($command.name)::#class($command.name)(#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_cpp($params.get($i)), #end#end#if( $last >= 0 )#param_declaration_cpp($params.get($last))#end): #klass($command)() {
#else
#class($command.name)::#class($command.name)(): #klass($command)() {
#end
    #foreach($param in $params)
m_$param.getName() = $param.getName();
    #end
    // Use Requires() here to declare subsystem dependencies
    // eg. Requires(Robot::chassis.get());
#@autogenerated_code("requires", "    ")
#parse("${exporter_path}Command-requires.cpp")
#end
#if ( $command.getProperty("Run When Disabled").getValue() )
    SetRunWhenDisabled(true);
#end
}