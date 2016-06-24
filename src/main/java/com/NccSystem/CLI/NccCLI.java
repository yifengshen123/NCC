package com.NccSystem.CLI;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.io.*;

public class NccCLI {

    private SshServer sshd;

    public NccCLI(Integer port){
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
        sshd.setPasswordAuthenticator(new NccPasswordAuthenticator());
        sshd.setShellFactory(new NccShellFactory());
    }

    public void start(){
        try {
            sshd.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
