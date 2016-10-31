package com;

import com.NccAPI.NccAPI;
import com.NccDhcp.NccDhcpServer;
import com.NccNetworkMonitor.NccNetworkMonitor;
import com.NccRadius.NccRadius;
import com.NccSystem.CLI.NccCLI;
import com.NccSystem.NccLogger;
import com.NccSystem.SQL.NccSQLPool;
import org.apache.commons.configuration.*;
import org.apache.log4j.*;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.*;

public class Ncc {

    private static NccRadius nccRadius;
    private static NccAPI nccAPI;
    private static NccDhcpServer nccDhcp;
    public static NccSQLPool sqlPool;
    private static NccLogger nccLogger = new NccLogger("MainLogger");
    private static Logger logger;
    public static String logLevel = "DEBUG";
    public static String logFile = "ncc.log";
    private static boolean moduleRadius = true;
    private static boolean moduleDHCP = true;
    private static boolean moduleCLI = true;
    private static boolean moduleAPI = true;
    private static boolean moduleIPTV = true;
    private static boolean moduleNetmon = false;
    public static boolean logQuery = false;
    public static Integer dhcpTimer = 1;
    public static Integer radiusTimer = 60;
    public static Integer dhcpUnbindedCleanupTime = 10;
    public static Integer radiusLogLevel = 0;
    public static Integer dhcpLogLevel = 0;
    public static boolean dhcpIgnoreBroadcast = true;
    public static Integer cliSshPort = 3270;
    public static boolean nccForceGC = false;
    public static Integer apiPort = 8032;

    public static String SQLLogfile = "sql.log";
    public static String radiusLogfile = "radius.log";
    public static String dhcpLogfile = "dhcp.log";
    public static String apiLogfile = "api.log";
    public static String cliLogfile = "cli.log";
    public static String iptvLogfile = "iptv.log";
    public static String netmonLogfile = "netmon.log";

    public static void main(String[] args) throws InterruptedException, SQLException, IOException {

        String dbHost, dbDbname, dbUser, dbPassword;
        String connectString;
        Integer dbPort;

        CompositeConfiguration config = new CompositeConfiguration();
        String current = new java.io.File(".").getCanonicalPath();

        try {
            config.addConfiguration(new SystemConfiguration());
            config.addConfiguration(new PropertiesConfiguration("config.properties"));

            logLevel = config.getString("log.level", "INFO");
            logFile = config.getString("log.file", "ncc.log");

            logger = nccLogger.setFilename(logFile);
            logger.setLevel(Level.toLevel(logLevel));

            SQLLogfile = config.getString("sql.logfile", "SQL.log");
            logQuery = config.getBoolean("sql.log.query", false);
            iptvLogfile = config.getString("iptv.logfile", "iptv.log");
            netmonLogfile = config.getString("netmon.logfile", "netmon.log");

            moduleRadius = config.getBoolean("module.radius", false);
            moduleDHCP = config.getBoolean("module.dhcp", false);
            moduleCLI = config.getBoolean("module.cli", true);
            moduleAPI = config.getBoolean("module.api", true);
            moduleIPTV = config.getBoolean("module.iptv", false);
            moduleNetmon = config.getBoolean("module.netmon", false);

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
            radiusTimer = config.getInt("radius.timer", 15);
            radiusLogLevel = config.getInt("radius.log.level", 5);
            radiusLogfile = config.getString("radius.logfile", "radius.log");
            nccRadius = new NccRadius();
            nccRadius.startServer();
        }

        if (moduleDHCP) {
            InetAddress localIP = InetAddress.getByName(config.getString("dhcp.server"));
            Integer port = config.getInt("dhcp.server.port", 67);

            dhcpTimer = config.getInt("dhcp.timer", 1);
            dhcpUnbindedCleanupTime = config.getInt("dhcp.unbinded.cleanup.time", 20);
            dhcpLogLevel = config.getInt("dhcp.log.level", 5);
            dhcpLogfile = config.getString("dhcp.logfile", "dhcp.log");
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

        if(moduleNetmon){
            NccNetworkMonitor netmon = new NccNetworkMonitor();
            netmon.start();
        }

        if (moduleCLI) {
            cliSshPort = config.getInt("cli.ssh.port");
            cliLogfile = config.getString("cli.logfile", "cli.log");

            NccCLI nccCLI = new NccCLI(cliSshPort);
            nccCLI.start();
        }

        if (moduleAPI) {
            apiPort = config.getInt("api.port", 8032);
            apiLogfile = config.getString("api.logfile", "api.log");

            nccAPI = new NccAPI(apiPort);
            nccAPI.start();
        }

    }
}
