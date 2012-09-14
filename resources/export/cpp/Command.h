#ifndef EXAMPLE_COMMAND_H
\#define EXAMPLE_COMMAND_H

\#include "../CommandBase.h"

/**
 *
 *
 * @author ExampleAuthor
 */
class #class($command.name): public CommandBase {
public:
	#class($command.name)();
	virtual void Initialize();
	virtual void Execute();
	virtual bool IsFinished();
	virtual void End();
	virtual void Interrupted();
};

#endif
