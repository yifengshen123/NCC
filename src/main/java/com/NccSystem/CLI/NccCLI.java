package com.NccSystem.CLI;

import com.Ncc;
import com.NccSystem.NccLogger;
import org.apache.log4j.Logger;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.io.*;

public class NccCLI {

    private static NccLogger nccLogger = new NccLogger("CLILogger");
    private static Logger logger = nccLogger.setFilename(Ncc.cliLogfile);

    private SshServer sshd;

    public NccCLI(Integer port){
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
        sshd.setPasswordAuthenticator(new NccPasswordAuthenticator());
        sshd.setShellFactory(new NccShellFactory());
    }

    public void start(){
        logger.info("Starting CLI on port " + Ncc.cliSshPort);
        try {
            sshd.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
