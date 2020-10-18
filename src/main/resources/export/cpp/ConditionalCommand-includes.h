#set($command = $helper.getByName($command_name, $robot))
#set($onTrue = $command.getProperty("On True Command").getValue())
#set($onFalse = $command.getProperty("On False Command").getValue())


\#include "commands/#class($onTrue).h"
\#include "commands/#class($onFalse).h"
\#include "frc/commands/ConditionalCommand.h"

