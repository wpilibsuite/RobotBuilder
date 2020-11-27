#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

\#include "commands/#class($command.name).h"

#if( $params.size() > 0 )
#class($command.name)::#class($command.name)(#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_cpp($params.get($i)), #end#end#if( $last >= 0 )#param_declaration_cpp($params.get($last))#end#if(${command.getProperty("Requires").getValue()} != "None"), #class(${command.getProperty("Requires").getValue()})* #variable(${command.getProperty("Requires").getValue().toLowerCase()})#end) :
#if( $len >= 0 )#foreach($i in [0..$len])
    m_$params.get($i).getName()($params.get($i).getName()),
#end
#end
#if( $last >= 0 )
    m_$params.get($last).getName()($params.get($last).getName())#end#if(${command.getProperty("Requires").getValue()} != "None"),
    m_#variable(${command.getProperty("Requires").getValue().toLowerCase()})(#variable(${command.getProperty("Requires").getValue().toLowerCase()}))#end
{
#elseif(${command.getProperty("Requires").getValue()} == "None")
#class($command.name)::#class($command.name)(){
#else
#class($command.name)::#class($command.name)(#class(${command.getProperty("Requires").getValue()})* #variable(${command.getProperty("Requires").getValue().toLowerCase()}))
:m_#variable(${command.getProperty("Requires").getValue().toLowerCase()})(#variable(${command.getProperty("Requires").getValue().toLowerCase()})){
#end

    // Use AddRequirements() here to declare subsystem dependencies
    // eg. AddRequirements(Robot::chassis.get());
    SetName("#class($command.name)");
    #if  (${command.getProperty("Requires").getValue()} != "None")
    AddRequirements(m_#variable(${command.getProperty("Requires").getValue().toLowerCase()}));
    #end
