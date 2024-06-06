#set($command = $helper.getByName($command_name, $robot))
#set($onTrue = $command.getProperty("On True Command").getValue())
#set($onFalse = $command.getProperty("On False Command").getValue())

\#include <frc2/command/ConditionalCommand.h>
\#include "commands/#class($onTrue).h"
\#include "commands/#class($onFalse).h"
#if (${command.getProperty("Requires").getValue()} != "None")
\#include "subsystems/#class(${command.getProperty("Requires").getValue()}).h"
#end