#set($onTrue = $helper.getByName($command.getProperty("On True Command").getValue(), $robot))
#set($onFalse = $helper.getByName($command.getProperty("On False Command").getValue(), $robot))

#parse("${exporter_path}Command-constructors.java")
      super(#new_command_instantiation_cc($onTrue), #new_command_instantiation_cc($onFalse), () ->