#header()

package ${package}.commands;
#set($command = $helper.getByName($command_name, $robot))

import edu.wpi.first.wpilibj.command.TimedCommand;
import ${package}.Robot;

#@autogenerated_code("imports", "")
#parse("${exporter_path}SetpointCommand-imports.java")
#end

/**
 *
 */
public class #class($command.name) extends TimedCommand {

#@autogenerated_code("variable_declarations", "    ")
#parse("${exporter_path}Command-variable-declarations.java")
#end

#@autogenerated_code("constructor", "    ")
#parse("${exporter_path}TimedCommand-constructors.java")
#end
#@autogenerated_code("requires", "        ")
#parse("${exporter_path}Command-requires.java")
#end
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {

    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
    }


    // Called once after isFinished returns true
    @Override
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }

    @Override
    public boolean runsWhenDisabled() {
#@autogenerated_code("disabled", "        ")
#parse("${exporter_path}Command-disabled.java")
#end
    }
}
