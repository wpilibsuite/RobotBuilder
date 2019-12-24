#set($command = $helper.getByName($command_name, $robot))

#header()

#pragma once

\#include "frc/commands/PIDCommand.h"
\#include "frc/commands/Subsystem.h"
\#include "Robot.h"

/**
 *
 *
 * @author ExampleAuthor
 */
class #class($command.name): public frc::PIDCommand {
public:
#@autogenerated_code("constructor", "    ")
#parse("${exporter_path}Command-constructor-header.h")
#end

    double ReturnPIDInput() override;
    void UsePIDOutput(double output) override;
	void Initialize() override;
	void Execute() override;
	bool IsFinished() override;
	void End() override;
	void Interrupted() override;

#@autogenerated_code("cmdpidgetters","	")
#parse("${exporter_path}Subsystem-pidgetters.h")
#end
private:
#@autogenerated_code("variables", "	")
#parse("${exporter_path}Command-constructor-variables.h")
#end
};

