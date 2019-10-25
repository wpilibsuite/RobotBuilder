#set($subsystem = $helper.getByName($subsystem_name, $robot))
#set($default_command = $subsystem.getProperty("Default Command").getValue())
#if($default_command != "None")\#include "Commands/#class($default_command).h"
#end
