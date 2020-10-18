#set($subsystem = $helper.getByName($subsystem_name, $robot))
#class($subsystem.name)::#class($subsystem.name)() : frc2::PIDSubsystem(frc2::PIDController(${subsystem.getProperty("P").getValue()}, ${subsystem.getProperty("I").getValue()}, ${subsystem.getProperty("D").getValue()})) {
    m_controller.SetTolerance(${subsystem.getProperty("Tolerance").getValue()});
    SetName("$subsystem_name");
#if($subsystem.getProperty("Limit Input").getValue())
    GetPIDController().SetInputRange(${subsystem.getProperty("Minimum Input").getValue()}, ${subsystem.getProperty("Maximum Input").getValue()});
#end
#if($subsystem.getProperty("Limit Output").getValue())
    GetPIDController().SetOutputRange(${subsystem.getProperty("Minimum Output").getValue()}, ${subsystem.getProperty("Maximum Output").getValue()});
#end
