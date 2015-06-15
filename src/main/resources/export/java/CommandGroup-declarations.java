#set($command = $helper.getByName($command-name, $robot))
#set($commandDescriptors = $command.getProperty("Commands").getValue())
#foreach( $cd in $commandDescriptors )
        add$cd.getOrder()(#command_instantiation($cd.getName(), $cd.getParameters().getValue()));
#end