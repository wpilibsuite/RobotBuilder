#set($command = $helper.getByName($command_name, $robot))

## parameters are hardcoded -- setpoint commands only take a single double argument
    private double m_setpoint;

    public #class($command.name)() {
        this(0);
    }

    public #class($command.name)(double setpoint) {
        super();
        this.m_setpoint = setpoint;

