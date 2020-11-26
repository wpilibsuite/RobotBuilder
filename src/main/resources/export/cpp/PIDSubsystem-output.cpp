#set($subsystem = $helper.getByName($subsystem_name, $robot))
#foreach ($component in $components)
#if ($component.name == $subsystem.getProperty("Output").getValue())
    #if($subsystem.getProperty("Limit Output").getValue())
    m_#variable($component.name).PIDWrite(std::clamp(output, ${subsystem.getProperty("Minimum Output").getValue()}, ${subsystem.getProperty("Maximum Output").getValue()}));
#end
    m_#variable($component.name).PIDWrite(output);
#end
#end