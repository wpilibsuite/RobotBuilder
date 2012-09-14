
\#include "RobotMap.h"
${helper.getImports($robot, "RobotMap")}

void RobotMap::init() {
#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
    #constructor($component)

    #livewindow($component)

    #extra($component)

#end
#end
}
