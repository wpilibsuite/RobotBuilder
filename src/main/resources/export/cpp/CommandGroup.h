#header()

#set($command = $helper.getByName($command-name, $robot))

#ifndef #constant($command.name)_H
\#define #constant($command.name)_H

\#include "Commands/CommandGroup.h"

/**
 *
 *
 * @author ExampleAuthor
 */
class #class($command.name): public CommandGroup {
public:	
	#class($command.name)();
};

#endif
