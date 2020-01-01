#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

## parameters are hardcoded -- setpoint PIDcommands only take a single double argument
#if ($command.getProperty("Requires").getValue() != "None")

    public #class($command.name)(double setpoint, #class($command.getProperty("Requires").getValue()) subsystem) {

#else
    public #class($command.name)(double setpoint) {
#end