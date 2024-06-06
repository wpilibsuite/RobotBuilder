#set( $subsystem = $helper.getByName($subsystem_name, $robot) )
#set( $constants = $subsystem.getProperty("Constants").getValue() )
#foreach( $constant in $constants )
    #declare_constant( $constant )
#end

    static constexpr const double kP = $subsystem.getProperty("P").getValue();
    static constexpr const double kI = $subsystem.getProperty("I").getValue();
    static constexpr const double kD = $subsystem.getProperty("D").getValue();
    static constexpr const double kF = $subsystem.getProperty("F").getValue();