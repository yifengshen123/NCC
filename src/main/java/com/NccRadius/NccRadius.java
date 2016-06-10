package com.NccRadius;

import com.NccAccounts.AccountData;
import com.NccAccounts.NccAccounts;
import com.NccAccounts.NccAccountsException;
import com.NccDhcp.NccDhcpException;
import com.NccDhcp.NccDhcpLeaseData;
import com.NccDhcp.NccDhcpLeases;
import com.NccNAS.NccNAS;
import com.NccNAS.NccNasData;
import com.NccNAS.NccNasException;
import com.NccPools.PoolData;
import com.NccSessions.NccSessions;
import com.NccSessions.NccSessionsException;
import com.NccSessions.SessionData;
import com.NccSystem.NccUtils;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.NccTariffScale.NccTariffScale;
import com.NccTariffScale.RateData;
import com.NccUsers.NccUsers;
import com.NccUsers.NccUsersException;
import com.NccUsers.NccUserData;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.log4j.Logger;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusClient;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RadiusServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

// TODO: 27.01.2016 acctInputGigawords support
// TODO: 27.01.2016 Concurrent sessions control

public class NccRadius extends RadiusServer {

    private static Logger logger = Logger.getLogger(NccRadius.class);
    private static Integer radAuthPort = 1812;
    private static Integer radAcctPort = 1813;
    private static String radSecret = "";
    private static NccUsers nccUsers;
    private static NccAccounts nccAccounts;
    private static boolean dbg = true;
    private int pktId = 1;

    public void disconnectUser(String nasIP, String userLogin, String sessionID) {
        try {
            NccNasData nasData = null;
            RadiusClient radiusClient = null;
            try {
                nasData = new NccNAS().getNasByIP(NccUtils.ip2long(nasIP));
                radiusClient = new RadiusClient(NccUtils.long2ip(nasData.nasIP), nasData.nasSecret);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            RadiusPacket pkt = new RadiusPacket();

            pkt.setPacketType(RadiusPacket.COA_REQUEST);
            pkt.setPacketIdentifier(pktId);
            pkt.addAttribute("User-Name", userLogin);
            pkt.addAttribute("Account-Info", "S" + userLogin);
            pkt.addAttribute("avpair", "subscriber:command=account-logoff");

            try {
                logger.debug("Sending CoA to " + NccUtils.long2ip(nasData.nasIP) + " for " + sessionID + " and username " + userLogin);
                pkt = radiusClient.communicate(pkt, 1700);
                pktId++;
                logger.debug("CoA reply: " + pkt.getPacketTypeName());

                if (pkt.getPacketType() == RadiusPacket.COA_ACK) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RadiusException e) {
                e.printStackTrace();
            }
        } catch (NccNasException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getSharedSecret(InetSocketAddress inetSocketAddress) {
        try {
            NccNAS nccNAS = new NccNAS();
            try {
                return nccNAS.getNasSecretByIP(NccUtils.ip2long(inetSocketAddress.getHostString()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } catch (NccNasException e) {
            e.printStackTrace();
        }

        return UUID.randomUUID().toString();
    }

    @Override
    public String getUserPassword(String s) {
        return null;
    }

    public NccRadius() {
        super();

        CompositeConfiguration config = new CompositeConfiguration();

        try {
            config.addConfiguration(new SystemConfiguration());
            config.addConfiguration(new PropertiesConfiguration("config.properties"));

            radAuthPort = config.getInt("radius.authport");
            radAcctPort = config.getInt("radius.acctport");
            radSecret = config.getString("radius.secret");

        } catch (ConfigurationException ce) {
            logger.fatal("config.properties not found.");
            System.exit(-1);
        }

        try {
            nccUsers = new NccUsers();
        } catch (NccUsersException e) {
            e.printStackTrace();
            logger.fatal(e.getMessage());
            System.exit(-1);
        }

        try {
            nccAccounts = new NccAccounts();
        } catch (NccAccountsException e) {
            e.printStackTrace();
            logger.fatal(e.getMessage());
            System.exit(-1);
        }

        setAuthPort(radAuthPort);
        setAcctPort(radAcctPort);
    }

    public RadiusPacket accountingRequestReceived(AccountingRequest accReq, InetSocketAddress accClient) {


        final AccountingRequest accountingRequest = accReq;
        final InetSocketAddress client = accClient;

        class AccountingThread implements Runnable {

            private volatile RadiusPacket radiusPacket = new RadiusPacket();

            public RadiusPacket getValue() {
                return radiusPacket;
            }

            @Override
            public void run() {

                Integer packetIdentifier = accountingRequest.getPacketIdentifier();
                Integer packetType = accountingRequest.getPacketType();
                Long statusType = 0L;

                try {
                    statusType = accountingRequest.getAcctStatusType();
                } catch (RadiusException e) {
                    e.printStackTrace();
                }

                switch (packetType) {
                    case RadiusPacket.ACCOUNTING_REQUEST:
                        radiusPacket.setPacketType(RadiusPacket.ACCOUNTING_RESPONSE);
                        radiusPacket.setPacketIdentifier(packetIdentifier);

                        String userLogin = "";
                        Integer userId = 0;
                        String sessionID = "";
                        String nasPort = "";
                        String nasIP = "";
                        String nasPortType = "";
                        String nasIdentifier = "";
                        String framedIP = "";
                        String framedMAC = "";
                        Long acctInputOctets;
                        Long acctOutputOctets;
                        Integer acctInputGigawords;
                        Integer acctOutputGigawords;
                        String acctSessionTime = "";
                        String callingStation = "";
                        String calledStation = "";
                        String framedProtocol = "";
                        String serviceType = "";
                        String eventTimestamp = "";
                        String acctAuthentic = "";

                        try {
                            userLogin = accountingRequest.getUserName();
                            sessionID = accountingRequest.getAttributeValue("Acct-Session-Id");
                            nasIP = accountingRequest.getAttributeValue("NAS-IP-Address");
                            nasPort = accountingRequest.getAttributeValue("NAS-Port");
                            nasIdentifier = accountingRequest.getAttributeValue("NAS-Identifier");
                            nasPortType = accountingRequest.getAttributeValue("NAS-Port-Type");
                            framedIP = accountingRequest.getAttributeValue("Framed-IP-Address");
                            callingStation = accountingRequest.getAttributeValue("Calling-Station-Id");
                            framedMAC = callingStation;
                            calledStation = accountingRequest.getAttributeValue("Called-Station-Id");
                            framedProtocol = accountingRequest.getAttributeValue("Framed-Protocol");
                            serviceType = accountingRequest.getAttributeValue("Service-Type");
                            eventTimestamp = accountingRequest.getAttributeValue("Event-Timestamp");
                            acctAuthentic = accountingRequest.getAttributeValue("Acct-Authentic");

                        } catch (RadiusException e) {
                            e.printStackTrace();
                        }


                        NccNasData nasData = null;
                        try {
                            try {
                                nasData = new NccNAS().getNasByIP(NccUtils.ip2long(nasIP));

                                if (nasData == null) {
                                    logger.error("NAS not found: " + nasIP);
                                    return;
                                }
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                        } catch (NccNasException e) {
                            e.printStackTrace();
                        }

                        SessionData sessionData = new SessionData();
                        NccDhcpLeaseData leaseData = null;
                        ArrayList<NccDhcpLeaseData> leases = null;

                        if (statusType.intValue() == AccountingRequest.ACCT_STATUS_TYPE_START) {

                            logger.info("Session start: '" + userLogin + "' sessionId=" + sessionID + " nasIP=" + nasIP + " nasPort=" + nasPort + " framedIP=" + framedIP + " framedMAC=" + framedMAC);

                            try {
                                SessionData checkSession = new NccSessions().getSession(sessionID);
                                if (checkSession != null) {
                                    logger.error("Duplicate session: '" + sessionID + "'");
                                    break;
                                }
                            } catch (NccSessionsException e) {
                                e.printStackTrace();
                            }

                            sessionData.nasId = nasData.id;

                            try {
                                sessionData.framedIP = NccUtils.ip2long(framedIP);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            sessionData.framedMAC = framedMAC;
                            sessionData.acctInputOctets = 0L;
                            sessionData.acctOutputOctets = 0L;
                            sessionData.sessionId = sessionID;
                            sessionData.startTime = System.currentTimeMillis() / 1000L;
                            sessionData.lastAlive = sessionData.startTime;
                            sessionData.sessionDuration = 0L;

                            try {
                                leaseData = new NccDhcpLeases().getLeaseByIP(NccUtils.ip2long(framedIP));

                                if (leaseData != null) {

                                    sessionData.framedMAC = leaseData.leaseClientMAC;
                                    sessionData.framedAgentId = leaseData.leaseRelayAgent;
                                    sessionData.framedCircuitId = leaseData.leaseCircuitID;
                                    sessionData.framedRemoteId = leaseData.leaseRemoteID;

                                    try {
                                        NccUserData userData = new NccUsers().getUser(leaseData.leaseUID);

                                        if (userData != null) {

                                            sessionData.userId = userData.id;
                                            sessionData.userTariff = userData.userTariff;
                                            try {
                                                new NccSessions().startSession(sessionData);
                                            } catch (NccSessionsException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (NccUsersException e) {
                                        e.printStackTrace();
                                    }

                                } else {

                                    logger.info("No lease found for session: " + sessionID + " login: " + userLogin);
                                }
                            } catch (NccDhcpException e) {
                                e.printStackTrace();
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }

                        } else if (statusType.intValue() == AccountingRequest.ACCT_STATUS_TYPE_STOP) {

                            logger.info("Session stop: '" + userLogin + "' sessionId='" + sessionID + "' nasIP=" + nasIP + " nasPort=" + nasPort + " framedIP=" + framedIP);

                            String terminateCause = accountingRequest.getAttributeValue("Acct-Terminate-Cause");

                            try {
                                sessionData = new NccSessions().getSession(sessionID);
                            } catch (NccSessionsException e) {
                                e.printStackTrace();
                            }

                            if (sessionData != null) {
                                sessionData.nasId = nasData.id;

                                switch (terminateCause) {
                                    case "User-Request":
                                        sessionData.terminateCause = 1;
                                        break;
                                    default:
                                        sessionData.terminateCause = 0;
                                        break;
                                }

                                sessionData.stopTime = System.currentTimeMillis() / 1000L;

                                try {

                                    new NccSessions().stopSession(sessionData);

                                } catch (NccSessionsException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                logger.error("Session not found: '" + sessionID + "'");

                            }

                        } else if (statusType.intValue() == AccountingRequest.ACCT_STATUS_TYPE_INTERIM_UPDATE) {

                            logger.debug("Session update: '" + userLogin + "' sessionId=" + sessionID + " nasIP=" + nasIP + " nasPort=" + nasPort + " framedIP=" + framedIP);

                            try {
                                leaseData = new NccDhcpLeases().getLeaseByIP(NccUtils.ip2long(framedIP));

                                if (leaseData != null) {

                                    logger.debug("Lease found: " + NccUtils.long2ip(leaseData.leaseIP));

                                    acctInputOctets = Long.parseLong(accountingRequest.getAttributeValue("Acct-Input-Octets"));
                                    acctOutputOctets = Long.parseLong(accountingRequest.getAttributeValue("Acct-Output-Octets"));

                                    logger.debug("In=" + acctInputOctets + " Out=" + acctOutputOctets);

                                    String attr = null;

                                    attr = accountingRequest.getAttributeValue("Acct-Input-Gigawords");
                                    if (attr != null) {
                                        acctInputGigawords = Integer.parseInt(attr);
                                    } else acctInputGigawords = 0;

                                    attr = accountingRequest.getAttributeValue("Acct-Output-Gigawords");
                                    if (attr != null) {
                                        acctOutputGigawords = Integer.parseInt(attr);
                                    } else acctOutputGigawords = 0;

                                    if (acctInputGigawords > 0) {
                                        acctInputOctets += acctInputGigawords * 1073741824L;
                                    }

                                    if (acctOutputGigawords > 0) {
                                        acctOutputOctets += acctOutputGigawords * 1073741824L;
                                    }

                                    logger.debug("Acct-Input-Gigawords=" + acctInputGigawords + " acctInputOctets=" + acctInputOctets);

                                    acctSessionTime = accountingRequest.getAttributeValue("Acct-Session-Time");

                                    try {
                                        sessionData = new NccSessions().getSession(sessionID);
                                    } catch (NccSessionsException e) {
                                        e.printStackTrace();
                                    }

                                    if (sessionData != null) {
                                        sessionData.acctInputOctets = acctInputOctets;
                                        sessionData.acctOutputOctets = acctOutputOctets;
                                        sessionData.lastAlive = System.currentTimeMillis() / 1000L;
                                        sessionData.sessionDuration = sessionData.lastAlive - sessionData.startTime;

                                        try {
                                            new NccSessions().updateSession(sessionData);
                                        } catch (NccSessionsException e) {
                                            e.printStackTrace();
                                        }
                                    } else {

                                        logger.error("Session not found: '" + sessionID + "'");
                                        try {
                                            SessionData resumeSession = new NccSessions().getSessionFromLog(sessionID);

                                            if (resumeSession != null) {

                                                resumeSession.acctInputOctets = acctInputOctets;
                                                resumeSession.acctOutputOctets = acctOutputOctets;

                                                resumeSession.lastAlive = System.currentTimeMillis() / 1000L;
                                                resumeSession.sessionDuration = Long.parseLong(acctSessionTime);

                                                ArrayList<Integer> ids = new NccSessions().resumeSession(resumeSession);
                                                if (ids != null) {
                                                    logger.info("Session '" + sessionID + "' resumed");
                                                }
                                            } else {
                                                logger.debug("No session to resume");
                                                disconnectUser(nasIP, userLogin, sessionID);
                                            }
                                        } catch (NccSessionsException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {

                                    logger.info("No lease found for session: " + sessionID + " login: " + userLogin);

                                    try {
                                        sessionData = new NccSessions().getSession(sessionID);

                                        if (sessionData != null) {
                                            // TODO: 4/19/16 set correct Terminate-Cause
                                            sessionData.terminateCause = 0;

                                            logger.debug("Session found: '" + sessionID + "'");

                                            //new NccSessions().stopSession(sessionData);
                                        } else {
                                            logger.error("Session not found: '" + sessionID + "'");
                                        }

                                        disconnectUser(nasIP, userLogin, sessionID);

                                    } catch (NccSessionsException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (NccDhcpException e) {
                                e.printStackTrace();
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }

                        }

                        break;
                    default:
                        break;
                }

            }
        }

        AccountingThread accountingThread = new AccountingThread();
        Thread thread = new Thread(accountingThread);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return accountingThread.getValue();
    }

    public RadiusPacket accessRequestReceived(AccessRequest accReq, InetSocketAddress accAddr) {

        final AccessRequest req = accReq;
        final InetSocketAddress addr = accAddr;

        class AccessThread implements Runnable {
            private volatile RadiusPacket radiusPacket = new RadiusPacket();

            public RadiusPacket getValue() {
                return radiusPacket;
            }

            @Override
            public void run() {
                long startTime = System.nanoTime();

                String reqUserName = req.getUserName();
                String reqUserPassword = req.getUserPassword();
                Integer reqPacketIdentifier = req.getPacketIdentifier();
                String reqServiceType = req.getServiceType();

                logger.debug("Access-Request '" + reqUserName + "' Service-Type '" + reqServiceType + "'");

                Integer packetType = RadiusPacket.ACCESS_REJECT;

                NccNasData nasData = new NccNasData();
                Long nasIP = null;

                try {
                    nasIP = NccUtils.ip2long(addr.getHostString());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                try {
                    NccNAS nccNAS = new NccNAS();

                    nasData = nccNAS.getNasByIP(nasIP);
                } catch (NccNasException e) {
                    logger.error("NAS error: " + e.getMessage());
                    return;
                }

                if (reqServiceType.equals("Outbound-User") || reqServiceType.equals("5")) {

                    NccDhcpLeases leases = new NccDhcpLeases();
                    try {

                        NccDhcpLeaseData leaseData = leases.getLeaseByIP(NccUtils.ip2long(reqUserName));
                        if (leaseData != null) {

                            if (leaseData.leaseUID == 0) {
                                packetType = RadiusPacket.ACCESS_REJECT;
                                logger.info("Login FAIL: userId=0");
                                return;
                            }

                            try {
                                NccUserData userData = new NccUsers().getUser(leaseData.leaseUID);

                                if (userData != null) {

                                    if (userData.userStatus == 0) {
                                        packetType = RadiusPacket.ACCESS_REJECT;
                                        logger.info("Login FAIL: [" + userData.userLogin + "] user disabled");
                                        return;
                                    }

                                    if (userData.userDeposit <= -userData.userCredit) {
                                        packetType = RadiusPacket.ACCESS_REJECT;
                                        radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                                        radiusPacket.setPacketType(packetType);
                                        logger.info("Login FAIL: [" + userData.userLogin + "] negative deposit");
                                        return;
                                    }

                                    RateData rateData = new NccTariffScale().getRate(userData.userTariff);

                                    if (rateData != null) {
                                        Integer inRate = rateData.inRate * 1000;
                                        Integer outRate = rateData.outRate * 1000;
                                        Integer inBurst = inRate / 2;
                                        Integer outBurst = outRate / 2;

                                        radiusPacket.addAttribute("SSG-Service-Info", "QU;" + inRate + ";" + inBurst + ";" + inRate + ";D;" + outRate + ";" + outBurst + ";" + outRate);
                                    }

                                    packetType = RadiusPacket.ACCESS_ACCEPT;
                                    radiusPacket.addAttribute("Acct-Interim-Interval", nasData.nasInterimInterval.toString());
                                    radiusPacket.addAttribute("avpair", "subscriber:accounting-list=ipoe-isg-aaa");
                                    radiusPacket.addAttribute("avpair", "ip:traffic-class=in access-group 101 priority 201");
                                    radiusPacket.addAttribute("avpair", "ip:traffic-class=out access-group 102 priority 201");
                                    radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                                    radiusPacket.setPacketType(packetType);
                                    logger.info("Login OK: " + reqUserName + " [" + userData.userLogin + "]");
                                    return;
                                }

                                packetType = RadiusPacket.ACCESS_REJECT;
                                radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                                radiusPacket.setPacketType(packetType);
                                logger.info("Login FAIL: [" + reqUserName + "] user not found");
                            } catch (NccUsersException e) {
                                e.printStackTrace();
                            }

                        } else {
                            packetType = RadiusPacket.ACCESS_REJECT;
                            radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                            radiusPacket.setPacketType(packetType);
                            logger.info("Login FAIL: [" + reqUserName + "] lease not found");
                        }

                    } catch (NccDhcpException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    packetType = RadiusPacket.ACCESS_REJECT;
                    radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                    radiusPacket.setPacketType(packetType);
                    logger.info("Login FAIL: " + reqUserName);
                    return;
                }

                try {
                    NccUserData userData = nccUsers.getUser(reqUserName);

                    try {
                        AccountData accountData = nccAccounts.getAccount(userData.accountId);

                        try {
                            if (req.verifyPassword(userData.userPassword)) {
                                if (userData.userStatus > 0) {

                                    if (accountData != null) if (accountData.accDeposit > -accountData.accCredit) {
                                        logger.info("Login OK: '" + reqUserName + "'");

                                        packetType = RadiusPacket.ACCESS_ACCEPT;

                                        try {
                                            ArrayList<PoolData> pools;

                                            NccTariffScale tariffScale = new NccTariffScale();

                                            pools = tariffScale.getTariffPools(userData.userTariff);

                                            Long framedIP = new NccSessions().getIPFromPool(pools);

                                            try {
                                                radiusPacket.addAttribute("Framed-IP-Address", NccUtils.long2ip(framedIP));
                                                radiusPacket.addAttribute("Framed-IP-Netmask", "255.255.255.255");
                                                radiusPacket.addAttribute("Acct-Interim-Interval", nasData.nasInterimInterval.toString());
                                            } catch (UnknownHostException e) {
                                                e.printStackTrace();
                                            }
                                        } catch (NccSessionsException e) {
                                            logger.info("Login FAIL: no enough IP in pools");
                                        }
                                    } else {
                                        logger.info("Login FAIL: deposit <= -credit");
                                    }
                                } else {
                                    logger.info("Login FAIL: user disabled");
                                }
                            } else {
                                logger.info("Login FAIL: incorrect userPassword for '" + reqUserName + "': '" + reqUserPassword + "' expected '" + userData.userPassword + "'");
                            }
                        } catch (RadiusException re) {
                            re.printStackTrace();
                        }
                    } catch (NccAccountsException e) {
                        logger.error(e.getMessage() + " for userId=" + userData.id);
                    }

                } catch (NccUsersException e) {
                    logger.info("User not found: '" + reqUserName + "'");
                }

                radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                radiusPacket.setPacketType(packetType);

                logger.debug("Response time: " + new DecimalFormat("#.#########").format((double) (System.nanoTime() - startTime) / 1000000000) + " sec.");

            }
        }

        AccessThread accessThread = new AccessThread();
        Thread thread = new Thread(accessThread);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return accessThread.getValue();
    }

    public void startServer() {

        Thread radiusWatchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                class NccRadiusTimer extends TimerTask {
                    @Override
                    public void run() {
                        try {

                            try {
                                CachedRowSetImpl rs = new NccQuery().selectQuery("SELECT id, sessionId FROM nccSessions WHERE lastAlive<UNIX_TIMESTAMP(NOW())-120");
                                if (rs != null) {
                                    try {
                                        while (rs.next()) {
                                            Integer id = rs.getInt("id");
                                            String sessionId = rs.getString("sessionId");

                                            logger.debug("Cleaning up session id=" + id + " sessionId=" + sessionId);

                                            SessionData sessionData = new NccSessions().getSession(sessionId);
                                            if (sessionData != null) {
                                                try {
                                                    new NccSessions().stopSession(sessionData);
                                                } catch (NccSessionsException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (NccQueryException e) {
                                e.printStackTrace();
                            }

                            ArrayList<SessionData> sessions = new NccSessions().getSessions();

                            for (SessionData sessionData : sessions) {
                                try {
                                    NccUserData userData = new NccUsers().getUser(sessionData.userId);

                                    if (userData != null) {
                                        try {
                                            NccNasData nasData = new NccNAS().getNAS(sessionData.nasId);

                                            if (userData.userStatus == 0) {
                                                logger.debug("Disconnecting user (user disabled): " + userData.userLogin + " sessionId: " + sessionData.sessionId);
                                                try {
                                                    disconnectUser(NccUtils.long2ip(nasData.nasIP), NccUtils.long2ip(sessionData.framedIP), sessionData.sessionId);
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            if (userData.userDeposit <= -userData.userCredit) {
                                                logger.debug("Disconnecting user (low deposit): " + userData.userLogin + " sessionId: " + sessionData.sessionId);
                                                try {
                                                    disconnectUser(NccUtils.long2ip(nasData.nasIP), NccUtils.long2ip(sessionData.framedIP), sessionData.sessionId);
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (NccNasException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                } catch (NccUsersException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (NccSessionsException e) {
                            e.printStackTrace();
                        }
                    }
                }

                Timer radiusTimer = new Timer();
                radiusTimer.schedule(new NccRadiusTimer(), 1000, 1000);
            }
        });

        radiusWatchThread.start();

        start(true, true);
    }
}
