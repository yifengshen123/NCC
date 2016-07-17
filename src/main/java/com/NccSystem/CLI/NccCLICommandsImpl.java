package com.NccSystem.CLI;

import com.NccAstraManager.NccAstraManager;
import com.NccAstraManager.TransponderData;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

public class NccCLICommandsImpl implements NccCLICommands {
    private PrintWriter writer;

    public NccCLICommandsImpl(PrintWriter writer){
        this.writer = writer;
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

        for (TransponderData d: astraManager.getTransponders()){
            writer.println(formatter.format("%-5d\t%-30s\r", d.id, d.transponderName));
        }
    }
}
