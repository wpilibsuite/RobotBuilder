#set($command = $helper.getByName($command-name, $robot))
        return Robot.#variable(${command.getProperty("Requires").getValue()}).onTarget();
