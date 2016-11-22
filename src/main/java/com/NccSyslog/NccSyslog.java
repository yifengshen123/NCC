package com.NccSyslog;

import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.impl.net.udp.UDPNetSyslogServerConfig;

public class NccSyslog {

    public NccSyslog(){
        SyslogServerConfigIF configIF = new UDPNetSyslogServerConfig();
        configIF.setHost("0.0.0.0");
        configIF.setPort(514);
        SyslogServer.createThreadedInstance("udp", configIF);
    }
}
