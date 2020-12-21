#set($command = $helper.getByName($command_name, $robot))

#class($command.name)::#class($command.name)(double timeout) : frc::TimedCommand(timeout) {
    // Use AddRequirements() here to declare subsystem dependencies
    // eg. AddRequirements(Robot::chassis.get());
    SetName("#class($command.name)");
    #if  (${command.getProperty("Requires").getValue()} != "None")
    AddRequirements(#variable(${command.getProperty("Requires").getValue().toLowerCase()}));
    #end
