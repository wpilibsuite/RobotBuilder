#set($command = $helper.getByName($command_name, $robot))
#set($commandDescriptors = $command.getProperty("Commands").getValue())
#foreach( $cd in $commandDescriptors )
        add$cd.getOrder()(#new_command_instantiation_nt($cd.getName(), $cd.getParameters().getValue()));
#end