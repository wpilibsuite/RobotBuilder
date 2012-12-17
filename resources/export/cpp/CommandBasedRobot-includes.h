#set($autonomous = $robot.getProperty("Autonomous Command").getValue())
#if($autonomous != "None")\#include "Commands/#class($autonomous).h"
${helper.getImports($robot, "Robot")}
