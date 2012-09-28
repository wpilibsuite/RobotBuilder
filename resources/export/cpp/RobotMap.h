#ifndef ROBOTMAP_H
\#define ROBOTMAP_H
\#include "WPILib.h"

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
class RobotMap {
public:
#foreach ($component in $components)
#if ($helper.exportsTo("RobotMap", $component))
	#declaration($component)

#end
#end
	static void init();
};
#endif
