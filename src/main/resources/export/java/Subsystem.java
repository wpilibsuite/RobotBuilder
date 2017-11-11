#header()

package ${package}.subsystems;
#set($subsystem = $helper.getByName($subsystem-name, $robot))
#macro( klass $cmd )#if( "#type($cmd)" == "" )Subsystem#else#type($cmd)#end#end

import ${package}.RobotMap;
import ${package}.commands.*;
import edu.wpi.first.wpilibj.command.Subsystem;
#@autogenerated_code("imports", "")
#parse("${exporter-path}Subsystem-imports.java")
#end


/**
 *
 */
public class #class($subsystem.name) extends #klass($subsystem) {

#@autogenerated_code("constants", "    ")
#parse("${exporter-path}Subsystem-constants.java")
#end

#@autogenerated_code("declarations", "    ")
#parse("${exporter-path}Subsystem-declarations.java")
#end

    @Override
    public void initDefaultCommand() {
#@autogenerated_code("default_command", "        ")
#parse("${exporter-path}Subsystem-default_command.java")
#end

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

}

