#set($autonomous = $robot.getProperty("Autonomous Command").getValue())
\#include "Commands/#class($autonomous).h"
${helper.getImports($robot, "Robot")}
