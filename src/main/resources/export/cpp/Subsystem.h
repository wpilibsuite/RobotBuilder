#set($subsystem = $helper.getByName($subsystem-name, $robot))
#macro( klass $cmd )#if( "#type($cmd)" == "" )frc::Subsystem#else#type($cmd)#end#end
#header()

#ifndef #constant($subsystem.name)_H
\#define #constant($subsystem.name)_H
\#include "frc/commands/Subsystem.h"
\#include "frc/WPILib.h"

/**
 *
 *
 * @author ExampleAuthor
 */
class #class($subsystem.name): public #klass($subsystem) {
private:
	// It's desirable that everything possible is private except
	// for methods that implement subsystem capabilities
#@autogenerated_code("declarations", "	")
#parse("${exporter_path}Subsystem-declarations.h")
#end
public:
	#class($subsystem.name)();
	void InitDefaultCommand() override;
	void Periodic() override;
#@autogenerated_code("cmdpidgetters", "	")
#parse("${exporter_path}Subsystem-pidgetters.h")
#end
#@autogenerated_code("constants", "	")
#parse("${exporter_path}Subsystem-constants.h")
#end


};

#endif
