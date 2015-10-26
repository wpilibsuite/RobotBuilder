#set( $subsystem = $helper.getByName($subsystem-name, $robot) )
#set( $constants = $subsystem.getProperty("Constants").getValue() )
#foreach( $constant in $constants )
#define_constant( $subsystem, $constant )
#end