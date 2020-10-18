#set($command = $helper.getByName($command_name, $robot))
#if ( $command.getProperty("Run When Disabled").getValue() )
    return true;
#else
    return false;
#end
