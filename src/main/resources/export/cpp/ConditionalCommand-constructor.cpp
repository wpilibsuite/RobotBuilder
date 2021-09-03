#set($command = $helper.getByName($command_name, $robot))
#set($onTrue = $command.getProperty("On True Command").getValue())
#set($onFalse = $command.getProperty("On False Command").getValue())


#class($command.name)::#class($command.name)( #if(${command.getProperty("Requires").getValue()} != "None") #class(${command.getProperty("Requires").getValue()})* #variable(${command.getProperty("Requires").getValue().toLowerCase()})#end): ConditionalCommand{#class($onTrue)(), #class($onFalse)(), [=]() -> bool

