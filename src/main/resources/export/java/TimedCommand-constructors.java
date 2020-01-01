#set($command = $helper.getByName($command_name, $robot))

## parameters are hardcoded -- Timed Commands only take a single double argument

#if ($command.getProperty("Requires").getValue() != "None")
    public #class($command.name)(#class($command.getProperty("Requires").getValue()) subsystem) {
        this(0, #class($command.getProperty("Requires").getValue()) subsystem);
    }

    public #class($command.name)(#class($command.getProperty("Requires").getValue()) subsystem, double timeout) {
        super(timeout);
#else
    public #class($command.name)() {
        this(0);
    }

    public #class($command.name)(double timeout) {
        super(timeout);
#end
#if ( $command.getProperty("Run When Disabled").getValue() )
        setRunWhenDisabled(true);
#end
