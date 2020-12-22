\#include <frc/smartdashboard/SendableChooser.h>
\#include <frc2/command/Command.h>

#foreach( $component in $components )
#if ($component.getBase().getType() == "Subsystem")
\#include "subsystems/#class(${component.name}).h"
#end
#end


${helper.getImports($robot, "RobotContainer")}
${helper.getImports($robot, "OI")}