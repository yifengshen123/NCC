package com;

import com.NccAPI.NccAPI;
import com.NccDhcp.NccDhcpServer;
import com.NccRadius.NccRadius;
import com.NccSystem.CLI.NccCLI;
import com.NccSystem.NccUtils;
import com.NccSystem.SQL.NccSQLPool;
import org.apache.commons.configuration.*;
import org.apache.log4j.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;

// TODO: 15.01.2016 override class RadiusServer to serve BindException exceptions

public class Ncc {

    private static NccRadius nccRadius;
    private static NccAPI nccAPI;
    private static NccDhcpServer nccDhcp;
    public static NccSQLPool sqlPool;
    private static Logger logger = Logger.getRootLogger();
    private static String logLevel = "DEBUG";
    public static String logFile = "NCC.log";
    private static boolean moduleRadius = true;
    private static boolean moduleDHCP = true;
    private static boolean moduleCLI = true;
    public static boolean logQuery = false;
    public static Integer dhcpTimer = 1;
    public static Integer radiusTimer = 60;
    public static Integer dhcpUnbindedCleanupTime = 10;
    public static Integer radiusLogLevel = 0;
    public static Integer dhcpLogLevel = 0;
    public static boolean dhcpIgnoreBroadcast = true;
    public static Integer cliSshPort = 3270;
    public static boolean nccForceGC = false;

    public static void main(String[] args) throws InterruptedException, SQLException, IOException {


        logger.setLevel(Level.toLevel(logLevel));

        String dbHost, dbDbname, dbUser, dbPassword;
        String connectString;
        Integer dbPort;

        CompositeConfiguration config = new CompositeConfiguration();
        String current = new java.io.File(".").getCanonicalPath();

        try {
            config.addConfiguration(new SystemConfiguration());
            config.addConfiguration(new PropertiesConfiguration("config.properties"));

            logLevel = config.getString("log.level");
            logFile = config.getString("log.file");
            logQuery = Boolean.valueOf(config.getString("log.query"));

            moduleRadius = Boolean.valueOf(config.getString("module.radius"));
            moduleDHCP = Boolean.valueOf(config.getString("module.dhcp"));
            moduleCLI = Boolean.valueOf(config.getString("module.cli"));

            logger.setLevel(Level.toLevel(logLevel));

            FileAppender fileAppender = new FileAppender();
            fileAppender.setName("NccFileLogger");
            fileAppender.setFile(logFile);
            fileAppender.setLayout(new PatternLayout("%d{ISO8601} [%-5p] %m%n"));
            fileAppender.setAppend(true);
            fileAppender.activateOptions();

            Logger.getRootLogger().addAppender(fileAppender);

            logger.info("NCC system loading...");

            dbHost = config.getString("db.host");
            dbPort = config.getInt("db.port");
            dbUser = config.getString("db.user");
            dbPassword = config.getString("db.password");
            dbDbname = config.getString("db.dbname");

            logger.debug("Got SQL config");

            logger.info("Init SQL pool: " + dbUser + "@" + dbHost);

            connectString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbDbname + "?useUnicode=yes&characterEncoding=UTF-8";
            sqlPool = new NccSQLPool(connectString, dbUser, dbPassword);

            logger.info("SQL pool initialized");

        } catch (ConfigurationException ce) {
            logger.fatal("Config file missing");
            System.out.println("Config file missing in " + current);
            System.exit(-1);
        }

        nccForceGC = config.getBoolean("ncc.global.gc_forced");

        if (moduleRadius) {
            logger.info("Starting Radius");
            radiusTimer = config.getInt("radius.timer");
            radiusLogLevel = config.getInt("radius.log.level");
            nccRadius = new NccRadius();
            nccRadius.startServer();
        }

        if (moduleDHCP) {
            InetAddress localIP = InetAddress.getByName(config.getString("dhcp.server"));

            logger.info("Starting DHCP");
            dhcpTimer = config.getInt("dhcp.timer");
            dhcpUnbindedCleanupTime = config.getInt("dhcp.unbinded.cleanup.time");
            dhcpLogLevel = config.getInt("dhcp.log.level");
            dhcpIgnoreBroadcast = config.getBoolean("dhcp.ignore.broadcast");
            nccDhcp = new NccDhcpServer(localIP, 67);
            nccDhcp.start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Stopping NCC server...");
                if (moduleRadius) nccRadius.stop();
                nccAPI.stop();
                sqlPool.close();
            }
        });

        if (moduleCLI) {
            cliSshPort = config.getInt("cli.ssh.port");
            logger.info("Starting CLI on port " + cliSshPort);
            NccCLI nccCLI = new NccCLI(cliSshPort);
            nccCLI.start();
        }

        logger.info("Starting API");
        nccAPI = new NccAPI();
        nccAPI.start();

    }
}
