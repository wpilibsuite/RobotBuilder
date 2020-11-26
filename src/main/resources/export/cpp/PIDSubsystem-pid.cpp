#set($subsystem = $helper.getByName($subsystem_name, $robot))
#class($subsystem.name)::#class($subsystem.name)() : frc2::PIDSubsystem(frc2::PIDController{kP, kI, kD}) {
    m_controller.SetTolerance(${subsystem.getProperty("Tolerance").getValue()});
    SetName("$subsystem_name");
#if($subsystem.getProperty("Limit Input").getValue())
    GetController().EnableContinuousInput(${subsystem.getProperty("Minimum Input").getValue()}, ${subsystem.getProperty("Maximum Input").getValue()});
#end
