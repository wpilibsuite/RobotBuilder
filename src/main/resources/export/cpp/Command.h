#set($command = $helper.getByName($command-name, $robot))
#macro( klass $cmd )#if( "#type($cmd)" == "" )frc::Command#else#type($cmd)#end#end
#header()

#ifndef #constant($command.name)_H
\#define #constant($command.name)_H


\#include "Commands/Subsystem.h"
\#include "../Robot.h"

/**
 *
 *
 * @author ExampleAuthor
 */
class #class($command.name): public #klass($command) {
public:
#@autogenerated_code("constructor", "    ")
#parse("${exporter-path}Command-constructor-header.h")
#end

	void Initialize() override;
	void Execute() override;
	bool IsFinished() override;
	void End() override;
	void Interrupted() override;

private:
#@autogenerated_code("variables", "	")
#parse("${exporter-path}Command-constructor-variables.h")
#end
};

#endif
