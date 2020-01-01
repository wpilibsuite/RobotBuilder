#if ($command.getProperty("Requires").getValue() != "None")
import ${package}.subsystems.#class($command.getProperty("Requires").getValue());
#end