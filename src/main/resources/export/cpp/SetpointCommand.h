#set($command = $helper.getByName($command_name, $robot))
#header()

#pragma once

#@autogenerated_code("constructor", "    ")
#parse("${exporter_path}SetpointCommand-includes.h")
#end

/**
 *
 *
 * @author ExampleAuthor
 */
class #class($command.name): public frc2::CommandHelper<frc2::CommandBase, #class($command.name)> {
public:
#@autogenerated_code("constructor", "    ")
#parse("${exporter_path}SetpointCommand-constructor-header.h")
#end

    void Initialize() override;
    void Execute() override;
    bool IsFinished() override;
    void End();
    void Interrupted();

private:
#@autogenerated_code("variables", "    ")
    double m_setpoint;
    #class(${command.getProperty("Requires").getValue()})* m_#variable(${command.getProperty("Requires").getValue().toLowerCase()});
#end
};

