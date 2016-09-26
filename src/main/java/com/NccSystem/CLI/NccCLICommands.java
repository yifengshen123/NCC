package com.NccSystem.CLI;

import com.NccAstraManager.TransponderData;

import java.util.ArrayList;

public interface NccCLICommands {
    public void exitCLI();
    public void sysShutdown();
    public void sysShutdown(Integer timeout);

    public void getAstraTransponders();
    public void runAstraTransponder(Integer id);
    public void restartAstraTransponder(Integer id);
}
