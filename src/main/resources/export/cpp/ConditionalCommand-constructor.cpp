#set($command = $helper.getByName($command_name, $robot))
#set($onTrue = $command.getProperty("On True Command").getValue())
#set($onFalse = $command.getProperty("On False Command").getValue())


#class($command.name)::#class($command.name)(): ConditionalCommand{#class($onTrue)(), #class($onFalse)(), [=]() -> bool

