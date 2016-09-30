package com.NccSystem.CLI;

import com.NccAstraManager.TransponderData;

import java.util.ArrayList;

public interface NccCLICommands {
    public void exitCLI();
    public void sysShutdown();
    public void sysShutdown(Integer timeout);

    public void getIptvTransponders();
    public void runIptvTransponder(Integer id);
    public void restartIptvTransponder(Integer id);
    public void showIptvActiveChannels(Integer id);
}
