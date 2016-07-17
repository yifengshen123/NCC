package com.NccSystem.CLI;

import com.NccAstraManager.TransponderData;

import java.util.ArrayList;

public interface NccCLICommands {
    public void sysShutdown();
    public void sysShutdown(Integer timeout);

    public void getAstraTransponders();
}
