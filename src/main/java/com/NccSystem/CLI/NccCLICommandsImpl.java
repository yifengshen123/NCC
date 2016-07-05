package com.NccSystem.CLI;

public class NccCLICommandsImpl implements NccCLICommands {
    public void sysShutdown() {
        System.out.println("Shutdown command");
    }

    public void sysShutdown(Integer timeout) {
        System.out.println("Shutdown in " + timeout + " seconds");
    }
}
