#set($command = $helper.getByName($command_name, $robot))
#set($params = $command.getProperty("Parameters").getValue())
#set($len = $params.size() - 2)
#set($last = $len + 1)



#if ($command.getProperty("Requires").getValue() != "None")

#if( $params.size() > 0 )
    public #class($command.name)(#if($command.getProperty("DoubleSupplier 1").getValue() != "None")DoubleSupplier doublesupplier1, #end#if($command.getProperty("DoubleSupplier 2").getValue() != "None")DoubleSupplier doublesupplier2, #end#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_java($params.get($i)), #end#end#param_declaration_java($params.get($last)), #class($command.getProperty("Requires").getValue()) subsystem) {
#else
    public #class($command.name)(#if($command.getProperty("DoubleSupplier 1").getValue() != "None")DoubleSupplier doublesupplier1, #end#if($command.getProperty("DoubleSupplier 2").getValue() != "None")DoubleSupplier doublesupplier2, #end#class($command.getProperty("Requires").getValue()) subsystem) {

#end
#else
#if( $params.size() > 0 )
    public #class($command.name)#if($command.getProperty("DoubleSupplier 1").getValue() != "None")DoubleSupplier doublesupplier1, #end#if($command.getProperty("DoubleSupplier 2").getValue() != "None")DoubleSupplier doublesupplier2, #end(#if( $len >= 0 )#foreach($i in [0..$len])#param_declaration_java($params.get($i)), #end#end#param_declaration_java($params.get($last))) {

#else
    public #class($command.name)(#if($command.getProperty("DoubleSupplier 1").getValue() != "None")DoubleSupplier doublesupplier1, #end#if($command.getProperty("DoubleSupplier 2").getValue() != "None")DoubleSupplier doublesupplier2, #end) {
#end
#end