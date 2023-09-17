#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

#set($command = $helper.getByName($command_name, $robot))
#set($actuator = $command.getProperty("Output").getValue())
#set($subsystem = $command.getProperty("Requires").getValue())
#foreach ($component in $components)
#if ($component.name == $actuator)
#set($Output = $component.name)
#end
#end

#set($command = $helper.getByName($command_name, $robot))
#set($sensor = $command.getProperty("Input").getValue())
#set($subsystem = $command.getProperty("Requires").getValue())
#foreach ($component in $components)
#if ($component.name == $sensor)
#set($Input = $component.name)
#end
#end

#if( $params.size() > 0 )
#class($command.name)::#class($command.name)(#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_cpp($params.get($i)), #end#end#if( $last >= 0 )#param_declaration_cpp($params.get($last))#end): #klass($command)() {
#else
#class($command.name)::#class($command.name)(#class(${command.getProperty("Requires").getValue()})* #variable(${command.getProperty("Requires").getValue().toLowerCase()})): frc2::CommandHelper<frc2::PIDCommand, #class($command.name)>(
frc::PIDController(double(${command.getProperty("P").getValue()}), double(${command.getProperty("I").getValue()}), double(${command.getProperty("D").getValue()})),
[this](){return #variable(${command.getProperty("Requires").getValue().toLowerCase()})-> Get#class($Input)().PIDGet();},
0, [this](double output){#variable(${command.getProperty("Requires").getValue().toLowerCase()})-> Get#class($Output)().PIDWrite(output);},
{#variable(${command.getProperty("Requires").getValue().toLowerCase()})}),
#variable(${command.getProperty("Requires").getValue().toLowerCase()}) (#variable(${command.getProperty("Requires").getValue().toLowerCase()})){
m_controller.SetTolerance(${command.getProperty("Tolerance").getValue()});
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
