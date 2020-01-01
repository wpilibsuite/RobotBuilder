#foreach ($component in $components)
#if ($component.subsystem == $subsystem.subsystem && $component != $subsystem)
        #livewindow($component)
        
        #extra($component)

        
#end
#end
