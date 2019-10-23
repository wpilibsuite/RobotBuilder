// Create some buttons
${Collections.reverse($components)}
#foreach( $component in $components )
#if ($helper.exportsTo("OI", $component)
    && "#type($component)" != "Joystick" 
    && ("#constructor($component)" != "" || "#extra($component)" != ""))
        #constructor($component)
        ${Collections.reverse($commands)}
        #foreach ($command in $commands)
                #if($command.name == $component.getProperty("Command").value)
                        #if ($command.getProperty("Requires").getValue() != "None")
                                //Does require Subsystem
                                //#required_subsystem($command)
                                #if( $command.getProperty("Parameter presets").getValue().isEmpty() )
                                        //No Parameters
                                        #variable($component.name).$component.getProperty("When to Run").getValue()(new #class($command.name)(m_#required_subsystem($command))); 
                                #else
                                        //Has Paramters
                                        #set($params = $component.getProperty("Parameters").getValue())
                                        #variable($component.name).$component.getProperty("When to Run").getValue()(new #class($command.name)(m_#required_subsystem($command),#command_params($params))); 
                                #end
                        #else
                                //Does Not Require Subsystem
                                #if( $command.getProperty("Parameter presets").getValue().isEmpty() )
                                        //No Parameters
                                        #variable($component.name).$component.getProperty("When to Run").getValue()(new #class($command.name)()); 
                                #else
                                        //Has Paramters
                                        #set($params = $component.getProperty("Parameters").getValue())
                                        #variable($component.name).$component.getProperty("When to Run").getValue()(new #class($command.name)(#command_params($params))); 
                                #end
                        #end
                #end
        #end
        
        


#end
#end

${Collections.reverse($components)}
        // SmartDashboard Buttons
#foreach( $component in $components )
#if ($component.getBase().getType() == "Command"
     && $component.getProperty("Button on SmartDashboard").getValue())
        #if( $component.getProperty("Parameter presets").getValue().isEmpty() )
                //No Parameters
                #if ($component.getProperty("Requires").getValue() != "None")
                        //Does Require Subsystem
                        SmartDashboard.putData("$component.getName()", new #class($component.getName())(m_#required_subsystem($component)));
                #else
                        //Does Not Require Subsystem
                        SmartDashboard.putData("$component.getName()", new #class($component.getName())());
                #end
        #else
                //Has Parameters
                #if ($component.getProperty("Requires").getValue() != "None")
                        //Does Require Subsystem
                        #foreach( $set in $component.getProperty("Parameter presets").getValue() )
                                SmartDashboard.putData("$component.getName(): $set.getName()", new #class($component.getName())(m_#required_subsystem($component),#command_params($set.getParameters())));
                        #end
                #else
                        //Does Not Require Subsystem
                        #foreach( $set in $component.getProperty("Parameter presets").getValue() )
                                SmartDashboard.putData("$component.getName(): $set.getName()", new #class($component.getName())(#command_params($set.getParameters())));
                        #end
                #end
        #end
#end
#end

