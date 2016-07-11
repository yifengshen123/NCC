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

            logLevel = config.getString("log.level", "INFO");
            logFile = config.getString("log.file", "NCC.log");
            logQuery = config.getBoolean("log.query", false);

            moduleRadius = config.getBoolean("module.radius", false);
            moduleDHCP = config.getBoolean("module.dhcp", false);
            moduleCLI = config.getBoolean("module.cli", true);

            logger.setLevel(Level.toLevel(logLevel));

            FileAppender fileAppender = new FileAppender();
            fileAppender.setName("NccFileLogger");
            fileAppender.setFile(logFile);
            fileAppender.setLayout(new PatternLayout("%d{ISO8601} [%-5p] %m%n"));
            fileAppender.setAppend(true);
            fileAppender.activateOptions();

            Logger.getRootLogger().addAppender(fileAppender);

            logger.info("NCC system loading...");

            dbHost = config.getString("db.host", "localhost");
            dbPort = config.getInt("db.port", 3306);
            dbUser = config.getString("db.user", "ncc");
            dbPassword = config.getString("db.password", "ncc");
            dbDbname = config.getString("db.dbname", "ncc");

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

        nccForceGC = config.getBoolean("ncc.global.gc_forced", false);

        if (moduleRadius) {
            logger.info("Starting Radius");
            radiusTimer = config.getInt("radius.timer", 15);
            radiusLogLevel = config.getInt("radius.log.level", 5);
            nccRadius = new NccRadius();
            nccRadius.startServer();
        }

        if (moduleDHCP) {
            InetAddress localIP = InetAddress.getByName(config.getString("dhcp.server"));
            Integer port = config.getInt("dhcp.server.port", 67);

            logger.info("Starting DHCP");
            dhcpTimer = config.getInt("dhcp.timer", 1);
            dhcpUnbindedCleanupTime = config.getInt("dhcp.unbinded.cleanup.time", 20);
            dhcpLogLevel = config.getInt("dhcp.log.level", 5);
            dhcpIgnoreBroadcast = config.getBoolean("dhcp.ignore.broadcast", true);
            nccDhcp = new NccDhcpServer(localIP, port);
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
