#set($command = $helper.getByName($command_name, $robot))

#class($command.name)::#class($command.name)(units::second_t timeout#if (${command.getProperty("Requires").getValue()} != "None"), #class(${command.getProperty("Requires").getValue()})* #variable(${command.getProperty("Requires").getValue().toLowerCase()})#end) : frc2::WaitCommand(timeout) {
    SetName("#class($command.name)");

