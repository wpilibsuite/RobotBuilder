#set($command = $helper.getByName($command-name, $robot))
#set($onTrue = $command.getProperty("On True Command").getValue())
#set($onFalse = $command.getProperty("On False Command").getValue())
    public #class($command.name)() {
	super(new #class($onTrue)(), new #class($onFalse)());