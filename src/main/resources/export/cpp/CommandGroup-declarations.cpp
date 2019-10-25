#set($command = $helper.getByName($command_name, $robot))
#set($commandDescriptors = $command.getProperty("Commands").getValue())
#foreach( $cd in $commandDescriptors )
        Add$cd.getOrder()(#command_instantiation($cd.getName(), $cd.getParameters().getValue()));
#end