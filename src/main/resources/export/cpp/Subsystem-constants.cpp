#set( $subsystem = $helper.getByName($subsystem_name, $robot) )
#set( $constants = $subsystem.getProperty("Constants").getValue() )
#foreach( $constant in $constants )
#define_constant( $subsystem, $constant )
#end