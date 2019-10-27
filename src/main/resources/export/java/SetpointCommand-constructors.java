#set($command = $helper.getByName($command_name, $robot))

## parameters are hardcoded -- setpoint commands only take a single double argument
#if ($command.getProperty("Requires").getValue() != "None")
        
    public #class($command.name)(#class($command.getProperty("Requires").getValue()) subsystem) {
        this(0, subsystem);
    }

    public #class($command.name)(double setpoint, #class($command.getProperty("Requires").getValue()) subsystem) {
        super();
        this.m_setpoint = setpoint;

#else
    public #class($command.name)() {
        this(0);
    }

    public #class($command.name)(double setpoint) {
        super();
        this.m_setpoint = setpoint;

       
#end

