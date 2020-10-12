\#include <frc/Joystick.h>
\#include <frc2/command/Command.h>
\#include <frc2/command/button/JoystickButton.h>

#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Button on SmartDashboard").getValue())
        #if( $component.getProperty("Parameter presets").getValue().isEmpty() )
                #if ($component.getProperty("Requires").getValue() != "None")
\#include "Subsystems/#class(${component.getProperty("Requires").getValue()}).h"
               #end
        #end
#end
#end


${helper.getImports($robot, "RobotContainer")}
${helper.getImports($robot, "OI")}