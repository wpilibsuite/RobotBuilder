#foreach ($component in $components)
#if ($component.subsystem == $subsystem.subsystem && $component != $subsystem)
        //#constructor($component)
        #livewindow($component)

        #extra($component)
        
        
#end
#end
