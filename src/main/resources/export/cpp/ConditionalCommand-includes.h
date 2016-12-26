#set($command = $helper.getByName($command-name, $robot))
#set($onTrue = $command.getProperty("On True Command").getValue())
#set($onFalse = $command.getProperty("On False Command").getValue())


\#include "Commands/#class($onTrue).h"
\#include "Commands/#class($onFalse).h"
\#include "Commands/ConditionalCommand.h"

