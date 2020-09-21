
\#include "frc2/command/CommandHelper.h"
\#include "frc2/command/CommandBase.h"

#if (${command.getProperty("Requires").getValue()} != "None")
\#include "Subsystems/#class(${command.getProperty("Requires").getValue()}).h"
#end