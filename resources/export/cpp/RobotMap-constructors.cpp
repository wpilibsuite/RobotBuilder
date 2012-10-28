	LiveWindow* lw = LiveWindow::GetInstance();

#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
	#constructor($component)

	#livewindow($component)

	#extra($component)

#end
#end

