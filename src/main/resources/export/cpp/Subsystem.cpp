#set($subsystem = $helper.getByName($subsystem_name, $robot))
#macro( klass $cmd )#if( "#type($cmd)" == "" )SendableSubsystemBase#elsefrc::#type($cmd)#end#end
#header()

#@autogenerated_code("includes", "")
#parse("${exporter_path}Subsystem-includes.cpp")
#end

#class($subsystem.name)::#class($subsystem.name)(){
    SetName("$subsystem.name");
#@autogenerated_code("declarations", "    ")
#parse("${exporter_path}Subsystem-declarations.cpp")
#end
}

void #class($subsystem.name)::Periodic() {
    // Put code here to be run every loop

}

void #class($subsystem.name)::SimulationPeriodic() {
    // This method will be called once per scheduler run when in simulation

}

#@autogenerated_code("cmdpidgetters", "")
#parse("${exporter_path}Subsystem-pidgetters.cpp")
#end


// Put methods for controlling this subsystem
// here. Call these from Commands.
