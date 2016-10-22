package com.NccSystem.CLI;

import com.NccIptvManager.*;
import com.NccSystem.NccUtils;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

public class NccCLICommandsImpl implements NccCLICommands {

    private static Logger logger = Logger.getLogger("CLILogger");

    private PrintWriter writer;

    public NccCLICommandsImpl(PrintWriter writer) {
        this.writer = writer;
    }

    public void exitCLI() {
        writer.println("Exiting CLI");
        NccShellFactory.exitFlag = true;
    }

    public void sysShutdown() {
        System.out.println("Shutdown command");
    }

    public void sysShutdown(Integer timeout) {
        System.out.println("Shutdown in " + timeout + " seconds");
    }

    public void showIptvTransponders() {
        NccIptvManager astraManager = new NccIptvManager();
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);

        for (TransponderData d : astraManager.getTransponders()) {
            writer.println(formatter.format("%-5d %-8d %s %-16s\r", d.id, d.transponderFreq, d.transponderPolarity, NccUtils.long2ip(d.serverIP)));
        }
    }

    public void runIptvTransponder(Integer id) {
        logger.info("Run transponder id=" + id);

        final NccIptvManager astraManager = new NccIptvManager();
        final Integer transponderId = id;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                astraManager.runTransponder(transponderId);
            }
        });
        t.start();
    }

    public void restartIptvTransponder(Integer id) {
        logger.info("Restart transponder id=" + id);

        final NccIptvManager astraManager = new NccIptvManager();
        final Integer transponderId = id;

        astraManager.stopTransponder(id);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                astraManager.runTransponder(transponderId);
            }
        });
        t.start();
    }

    public void showIptvActiveChannels(Integer id) {
        ArrayList<ActiveChannel> channels = new NccIptvManager().getActiveChannelsByTransponderId(id);
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);

        writer.println(formatter.format("%-36s %-15s %-5s %-5s %-5s\r", "Channel name", "Channel IP", "Bps", "CC", "PES"));
        for (ActiveChannel c : channels) {
            writer.println(formatter.format("%-36s %-15s %-5d %-5d %-5d\r", c.channelData.channelName, NccUtils.long2ip(c.channelData.channelIP), c.bitrate, c.ccCount, c.scrambledCount));
        }
    }

    public void showIptvCams(){
        ArrayList<CamData> cams = new NccIptvManager().getCam();
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);

        writer.println(formatter.format("%-20s %-15s %-10s %-15s %-15s\r", "CAM name", "CAM server", "CAM port", "CAM user", "CAM password"));
        for (CamData c : cams) {
            writer.println(formatter.format("%-20s %-15s %-10d %-15s %-15s\r", c.camName, c.camServer, c.camPort, c.camUser, c.camPassword));
        }

    }

    public void showIptvChannels(){
        ArrayList<ChannelData> channels = new NccIptvManager().getChannels();
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);

        writer.println(formatter.format("%-25s %-5s %-6s %-15s\r", "Channel name", "PNR", "Trans", "CAM"));
        for (ChannelData c : channels) {
            writer.println(formatter.format("%-25s %-5d %-5d%s %-15s\r", c.channelName, c.channelPnr, c.transponderFreq, c.transponderPolarity, c.camName));
        }

    }
}
