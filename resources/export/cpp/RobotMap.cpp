
\#include "RobotMap.h"
${helper.getImports($robot, "RobotMap")}

#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
#type($component)* RobotMap::#constant($component.fullName) = 0;
#end
#end

void RobotMap::init() {
#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
    #constructor($component)

    #livewindow($component)

    #extra($component)

#end
#end
}
