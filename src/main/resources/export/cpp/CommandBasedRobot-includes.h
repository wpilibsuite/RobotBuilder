#set($autonomous = $robot.getProperty("Autonomous Command").getValue())
#if($autonomous != "None")\#include "Commands/#class($autonomous).h"
#end
#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Autonomous Selection").getValue())
\#include "Commands/#class($component.getName()).h"
#end
#end
${helper.getImports($robot, "Robot")}
