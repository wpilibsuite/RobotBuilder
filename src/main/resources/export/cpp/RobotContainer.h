#set($command = $robot.getProperty("Autonomous Command").getValue())

#header()

#pragma once



#@autogenerated_code("includes", "	")
#parse("${exporter_path}OI-includes.h")
#end

class RobotContainer {

public:
	RobotContainer();
	frc2::Command* GetAutonomousCommand();

#@autogenerated_code("prototypes", "	")
#parse("${exporter_path}OI-prototypes.h")
#end

private:

#if($component.getBase().getType() == "Command" && !($component.getProperty("Parameters").getValue().isEmpty()))
double m_setpoint;
#end

#@autogenerated_code("declarations", "	")
#parse("${exporter_path}OI-declarations.h")
#end

#class($command) m_autonomousCommand;

void ConfigureButtonBindings();
};