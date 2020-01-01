#foreach ($command in $commands)
## sort through the command and get the imports required
#end
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
${helper.getImports($robot, "OI")}