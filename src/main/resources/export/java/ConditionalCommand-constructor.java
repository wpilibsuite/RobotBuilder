#set($command = $helper.getByName($command_name, $robot))
#set($onTrue = $command.getProperty("On True Command").getValue())
#set($onFalse = $command.getProperty("On False Command").getValue())
    public #class($command.name)() {
      super(new #class($onTrue)(), new #class($onFalse)());
#if ( $command.getProperty("Run When Disabled").getValue() )
      setRunWhenDisabled(true);
#end