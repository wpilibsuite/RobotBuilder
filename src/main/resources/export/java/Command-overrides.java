#if ( $command.getProperty("Run When Disabled").getValue() )
    @Override
    public boolean runsWhenDisabled() {
        return true;
    }

#end