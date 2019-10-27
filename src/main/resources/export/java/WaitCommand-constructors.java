#set($command = $helper.getByName($command_name, $robot))

## parameters are hardcoded -- Timed Commands only take a single double argument

#if ($command.getProperty("Requires").getValue() != "None")
    public #class($command.name)(double timeout, #class($command.getProperty("Requires").getValue()) subsystem) {
        super(timeout);
#else
    public #class($command.name)(double timeout) {
        super(timeout);
#end
#if ( $command.getProperty("Run When Disabled").getValue() )
        setRunWhenDisabled(true);
#end
