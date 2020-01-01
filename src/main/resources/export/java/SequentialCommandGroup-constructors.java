#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)

public #class($command.name)() {
###foreach ($command in $commands)
##        #if ($command.getProperty("Requires").getValue() != "None")
 ##               #class($command.getProperty("Requires").getValue()) 
 ##               //Claw claw
 ##       #end
###end) {