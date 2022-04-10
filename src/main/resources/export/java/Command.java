#header()
// ROBOTBUILDER TYPE: Command.

package ${package}.commands;
#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#macro( klass $cmd )#if( "#type($cmd)" == "" )CommandBase#else#type($cmd)#end#end
import edu.wpi.first.wpilibj2.command.CommandBase;
import java.util.function.DoubleSupplier;

#@autogenerated_code("imports", "")
#parse("${exporter_path}Command-imports.java")
#end

/**
 *
 */
public class #class($command.name) extends #klass($command) {

#@autogenerated_code("variable_declarations", "    ")
#parse("${exporter_path}Command-variable-declarations.java")
#end

#@autogenerated_code("constructors", "    ")
#parse("${exporter_path}Command-constructors.java")
#end
#@autogenerated_code("variable_setting", "        ")
#parse("${exporter_path}Command-variable-setting.java")
#end
#@autogenerated_code("requires", "        ")
#parse("${exporter_path}Command-requires.java")
#end
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean runsWhenDisabled() {
#@autogenerated_code("disabled", "        ")
#parse("${exporter_path}Command-disabled.java")
#end
    }
}
