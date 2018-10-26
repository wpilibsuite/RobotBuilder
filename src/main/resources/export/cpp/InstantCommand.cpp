#set($command = $helper.getByName($command-name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#header()

\#include "Commands/#class($command.name).h"

#@autogenerated_code("constructor", "")
#parse("${exporter-path}InstantCommand-constructor.cpp")
#end

// Called once when this command runs
void #class($command.name)::Initialize() {

}

