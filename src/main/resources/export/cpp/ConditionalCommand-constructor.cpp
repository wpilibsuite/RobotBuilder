#set($command = $helper.getByName($command_name, $robot))
#set($onTrue = $command.getProperty("On True Command").getValue())
#set($onFalse = $command.getProperty("On False Command").getValue())


#class($command.name)::#class($command.name)(): ConditionalCommand(new #class($onTrue), new #class($onFalse)) {
#if ( $command.getProperty("Run When Disabled").getValue() )
    SetRunWhenDisabled(true);
#end
}
