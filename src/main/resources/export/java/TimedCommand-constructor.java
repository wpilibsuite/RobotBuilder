#set($command = $helper.getByName($command-name, $robot))

## parameters are hardcoded -- Timed Commands only take a single double argument

    public #class($command.name)() {
        this(0);
    }

    public #class($command.name)(double timeout) {
        super(timeout);
        setRunWhenDisabled($command.getProperty("Run When Disabled").getValue());
