package com.NccSystem.CLI;

import com.NccAstraManager.NccAstraManager;
import com.NccAstraManager.TransponderData;
import com.NccSystem.NccUtils;
import com.NccUsers.NccUserData;
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

    public void getAstraTransponders() {
        NccAstraManager astraManager = new NccAstraManager();
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);

        for (TransponderData d : astraManager.getTransponders()) {
            writer.println(formatter.format("%-5d %-8d %s %-16s\r", d.id, d.transponderFreq, d.transponderPolarity, NccUtils.long2ip(d.serverIP)));
        }
    }

    public void runAstraTransponder(Integer id) {
        logger.info("Run transponder id=" + id);

        final NccAstraManager astraManager = new NccAstraManager();
        final Integer transponderId = id;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                astraManager.runTransponder(transponderId);
            }
        });
        t.start();
    }

    public void restartAstraTransponder(Integer id) {
        logger.info("Restart transponder id=" + id);

        final NccAstraManager astraManager = new NccAstraManager();
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

    public void showAstraActiveChannels(Integer id) {
        ArrayList<NccAstraManager.ActiveChannel> channels = new NccAstraManager().getActiveChannelsByTransponderId(id);
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);

        writer.println(formatter.format("%-36s %-15s %-5s %-5s %-5s\r", "Channel name", "Channel IP", "Bps", "CC", "PES"));
        for (NccAstraManager.ActiveChannel c : channels) {
            writer.println(formatter.format("%-36s %-15s %-5d %-5d %-5d\r", c.channelData.channelName, NccUtils.long2ip(c.channelData.channelIP), c.bitrate, c.ccCount, c.scrambledCount));
        }
    }
}
