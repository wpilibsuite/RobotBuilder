#set( $subsystem = $helper.getByName($subsystem-name, $robot) )
#set( $constants = $subsystem.getProperty("Constants").getValue() )
#foreach( $constant in $constants )
	#declare_constant( $constant )
#end