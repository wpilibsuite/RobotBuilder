#set($command = $helper.getByName($command_name, $robot))
#header()

\#include "Commands/#class($command.name).h"

#@autogenerated_code("constructor", "    ")
#parse("${exporter_path}ConditionalCommand-constructor.cpp")
#end

}

bool #class($command.name)::Condition(){
	return false;
}
