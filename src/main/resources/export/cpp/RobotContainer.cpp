#header()

\#include "RobotContainer.h"
\#include <frc2/command/ParallelRaceGroup.h>
\#include <frc/smartdashboard/SmartDashboard.h>

#set($cmd = $robot.getProperty("Autonomous Command").getValue())

#foreach ($component in $components)
#if ($helper.exportsTo("OI", $component)
     && "#type($component)" != "" 
     && "#type($component)" == "Joystick")
    #set($joy = $component)
#end
#end

RobotContainer::RobotContainer() : m_autonomousCommand(
#@autogenerated_code("constructor", "    ")
#parse("${exporter_path}RC-constructors.cpp")
#end

    // Process operator interface input here.
#@autogenerated_code("oi-constructor", "    ")
#parse("${exporter_path}OI-constructors.cpp")
#end

    ConfigureButtonBindings();

#@autogenerated_code("default-commands", "    ")
#parse("${exporter_path}Subsystem-default_command.cpp")
#end

}

void RobotContainer::ConfigureButtonBindings() {
#@autogenerated_code("buttons", "    ")
#parse("${exporter_path}RC-buttons.cpp")
#end
}

#@autogenerated_code("functions", "")
#parse("${exporter_path}OI-functions.cpp")
#end


frc2::Command* RobotContainer::GetAutonomousCommand() {
  // An example command will be run in autonomous
  return &m_autonomousCommand;
}