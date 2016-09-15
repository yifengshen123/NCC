package com.NccRadius;

import com.Ncc;
import com.NccAccounts.AccountData;
import com.NccAccounts.NccAccounts;
import com.NccAccounts.NccAccountsException;
import com.NccDhcp.NccDhcpException;
import com.NccDhcp.NccDhcpLeaseData;
import com.NccDhcp.NccDhcpLeases;
import com.NccNAS.NccNAS;
import com.NccNAS.NccNasData;
import com.NccNAS.NccNasException;
import com.NccPools.NccPoolData;
import com.NccSessions.NccSessionData;
import com.NccSessions.NccSessions;
import com.NccSessions.NccSessionsException;
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
import java.net.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class NccRadius extends RadiusServer {

    private static Logger logger = Logger.getLogger(NccRadius.class);
    private static Integer radAuthPort = 1812;
    private static Integer radAcctPort = 1813;
    private static String radSecret = "";
    private static NccUsers nccUsers;
    private static NccAccounts nccAccounts;
    private static int pktId = 1;

    public static void disconnectUser(String nasIP, String userLogin, String sessionID) {
        try {
            NccNasData nasData = null;
            RadiusClient radiusClient = null;
            nasData = new NccNAS().getNasByIP(NccUtils.ip2long(nasIP));
            radiusClient = new RadiusClient(NccUtils.long2ip(nasData.nasIP), nasData.nasSecret);
            RadiusPacket pkt = new RadiusPacket();

            pkt.setPacketType(RadiusPacket.COA_REQUEST);
            pkt.setPacketIdentifier(pktId);
            pkt.addAttribute("User-Name", userLogin);
            pkt.addAttribute("Account-Info", "S" + userLogin);
            pkt.addAttribute("avpair", "subscriber:command=account-logoff");

            try {
                if (Ncc.radiusLogLevel >= 3)
                    logger.info("Sending CoA to " + NccUtils.long2ip(nasData.nasIP) + " for " + sessionID + " and username " + userLogin);
                pkt = radiusClient.communicate(pkt, 1700);
                pktId++;
                if (Ncc.radiusLogLevel >= 3)
                    logger.info("CoA reply: " + pkt.getPacketTypeName());

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
            String secret = nccNAS.getNasSecretByIP(NccUtils.ip2long(inetSocketAddress.getHostString()));
            logger.debug("Getting secret for NAS: " + inetSocketAddress.getHostString() + " secret: " + secret);
            return secret;
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

        nccAccounts = new NccAccounts();

        setAuthPort(radAuthPort);
        setAcctPort(radAcctPort);
    }

    @Override
    public void listen(DatagramSocket s) {
        DatagramPacket packetIn = new DatagramPacket(new byte[RadiusPacket.MAX_PACKET_LENGTH], RadiusPacket.MAX_PACKET_LENGTH);
        while (true) {
            try {
                // receive packet
                try {
                    logger.trace("about to call socket.receive()");
                    s.receive(packetIn);
                    if (logger.isDebugEnabled())
                        if (Ncc.radiusLogLevel >= 7) logger.debug("receive buffer size = " + s.getReceiveBufferSize());
                } catch (SocketException se) {
                    if (closing) {
                        // end thread
                        logger.info("got closing signal - end listen thread");
                        return;
                    } else {
                        // retry s.receive()
                        logger.error("SocketException during s.receive() -> retry", se);
                        continue;
                    }
                }

                // check client
                InetSocketAddress localAddress = (InetSocketAddress) s.getLocalSocketAddress();
                InetSocketAddress remoteAddress = new InetSocketAddress(packetIn.getAddress(), packetIn.getPort());
                String secret = getSharedSecret(remoteAddress);
                if (secret == null) {
                    if (logger.isInfoEnabled())
                        logger.info("ignoring packet from unknown client " + remoteAddress + " received on local address " + localAddress);
                    continue;
                }

                // parse packet
                RadiusPacket request = makeRadiusPacket(packetIn, secret);
                if (logger.isDebugEnabled() && Ncc.radiusLogLevel >= 6) {
                    logger.info("received packet from " + remoteAddress + " on local address " + localAddress);
                    if (Ncc.radiusLogLevel >= 7)
                        logger.info("RAW packet: " + request);
                }

                // handle packet
                logger.trace("about to call RadiusServer.handlePacket()");
                RadiusPacket response = handlePacket(localAddress, remoteAddress, request, secret);

                // send response
                if (response != null) {
                    if (logger.isDebugEnabled())
                        if (Ncc.radiusLogLevel >= 6) logger.info("send response: " + response);
                    DatagramPacket packetOut = makeDatagramPacket(response, secret, remoteAddress.getAddress(), packetIn.getPort(), request);
                    s.send(packetOut);
                } else
                    logger.debug("no response sent");
            } catch (SocketTimeoutException ste) {
                // this is expected behaviour
                logger.trace("normal socket timeout");
            } catch (IOException ioe) {
                // error while reading/writing socket
                logger.error("communication error", ioe);
            } catch (RadiusException re) {
                // malformed packet
                logger.error("malformed Radius packet", re);
            }
        }
    }

    public RadiusPacket accountingRequestReceived(AccountingRequest accReq, InetSocketAddress accClient) {


        final AccountingRequest accountingRequest = accReq;
        final InetSocketAddress client = accClient;


        AccountingThread accountingThread = new AccountingThread(accountingRequest);
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

                if (Ncc.radiusLogLevel >= 6)
                    logger.info("Access-Request '" + reqUserName + "' Service-Type '" + reqServiceType + "'");

                Integer packetType = RadiusPacket.ACCESS_REJECT;

                NccNasData nasData;
                Long nasIP = NccUtils.ip2long(addr.getHostString());

                try {
                    NccNAS nccNAS = new NccNAS();

                    nasData = nccNAS.getNasByIP(nasIP);
                } catch (NccNasException e) {
                    logger.error("NAS error: " + e.getMessage());
                    return;
                }

                if (reqServiceType.equals("Outbound-User") || reqServiceType.equals("5")) {

                    logger.debug("Outbound-User");

                    NccDhcpLeases leases = new NccDhcpLeases();
                    try {

                        NccDhcpLeaseData leaseData = leases.getLeaseByIP(NccUtils.ip2long(reqUserName));
                        if (leaseData != null) {

                            if (leaseData.leaseUID == 0) {
                                radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                                radiusPacket.setPacketType(RadiusPacket.ACCESS_REJECT);
                                logger.info("Login FAIL: userId=0");
                                return;
                            }

                            logger.debug("Found lease data");

                            try {
                                NccUserData userData = new NccUsers().getUser(leaseData.leaseUID);

                                if (userData != null) {

                                    logger.debug("Found user data");

                                    if (userData.userStatus == 0) {
                                        radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                                        radiusPacket.setPacketType(RadiusPacket.ACCESS_REJECT);
                                        logger.info("Login FAIL: [" + userData.userLogin + "] user disabled");
                                        return;
                                    }

                                    if (Math.floor(userData.userDeposit) <= -Math.floor(userData.userCredit)) {
                                        radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                                        radiusPacket.setPacketType(RadiusPacket.ACCESS_REJECT);
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
                                    radiusPacket.addAttribute("Idle-Timeout", nasData.nasIdleTimeout.toString());
                                    radiusPacket.addAttribute("avpair", "subscriber:accounting-list=ipoe-isg-aaa");
                                    radiusPacket.addAttribute("avpair", "ip:traffic-class=in access-group " + nasData.nasAccessGroupIn.toString() + " priority 201");
                                    radiusPacket.addAttribute("avpair", "ip:traffic-class=out access-group " + nasData.nasAccessGroupOut.toString() + " priority 201");
                                    radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                                    radiusPacket.setPacketType(packetType);
                                    logger.info("Login OK: " + reqUserName + " [" + userData.userLogin + "]");
                                    return;
                                }

                                radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                                radiusPacket.setPacketType(RadiusPacket.ACCESS_REJECT);
                                logger.info("Login FAIL: [" + reqUserName + "] user not found");
                            } catch (NccUsersException e) {
                                logger.debug("getUser error: " + e.getMessage());
                                e.printStackTrace();
                            }

                        } else {
                            radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                            radiusPacket.setPacketType(RadiusPacket.ACCESS_REJECT);
                            logger.info("Login FAIL: [" + reqUserName + "] lease not found");
                        }

                    } catch (NccDhcpException e) {
                        e.printStackTrace();
                    }

                    radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                    radiusPacket.setPacketType(RadiusPacket.ACCESS_REJECT);
                    logger.info("Login FAIL: " + reqUserName);
                    return;
                }

                try {
                    NccUserData userData = nccUsers.getUser(reqUserName);

                    AccountData accountData = nccAccounts.getAccount(userData.accountId);
                    try {
                        if (req.verifyPassword(userData.userPassword)) {

                            logger.debug("Passwords equals");

                            if (userData.userStatus > 0) {

                                logger.debug("userStatus OK");

                                if (accountData != null) {
                                    if (accountData.accDeposit > -accountData.accCredit) {
                                        logger.info("Login OK: '" + reqUserName + "'");

                                        packetType = RadiusPacket.ACCESS_ACCEPT;

                                        try {
                                            ArrayList<NccPoolData> pools;

                                            NccTariffScale tariffScale = new NccTariffScale();

                                            pools = tariffScale.getTariffPools(userData.userTariff);

                                            Long framedIP = new NccSessions().getIPFromPool(pools);

                                            radiusPacket.addAttribute("Framed-IP-Address", NccUtils.long2ip(framedIP));
                                            radiusPacket.addAttribute("Framed-IP-Netmask", "255.255.255.255");
                                            radiusPacket.addAttribute("Acct-Interim-Interval", nasData.nasInterimInterval.toString());
                                        } catch (NccSessionsException e) {
                                            logger.info("Login FAIL: no enough IP in pools");
                                        }
                                    } else {
                                        logger.info("Login FAIL: deposit <= -credit");
                                    }
                                } else {
                                    logger.info("Login FAIL: accountData==NULL");
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

                } catch (NccUsersException e) {
                    logger.info("User not found: '" + reqUserName + "'");
                }

                radiusPacket.setPacketIdentifier(reqPacketIdentifier);
                radiusPacket.setPacketType(packetType);

                if (Ncc.radiusLogLevel >= 6)
                    logger.info("Response time: " + new DecimalFormat("#.#########").format((double) (System.nanoTime() - startTime) / 1000000000) + " sec.");

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

                                            if (Ncc.radiusLogLevel >= 6)
                                                logger.info("Cleaning up session id=" + id + " sessionId=" + sessionId);

                                            NccSessionData sessionData = new NccSessions().getSession(sessionId);
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

                            ArrayList<NccSessionData> sessions = new NccSessions().getSessions();

                            for (NccSessionData sessionData : sessions) {

                                if (Ncc.radiusLogLevel >= 7) logger.info("Checking session id=" + sessionData.id);

                                try {
                                    NccUserData userData = new NccUsers().getUser(sessionData.userId);

                                    if (userData != null) {

                                        if (Ncc.radiusLogLevel >= 7)
                                            logger.info("User: " + userData.userLogin + " deposit=" + userData.userDeposit + " credit=" + userData.userCredit);

                                        try {
                                            NccNasData nasData = new NccNAS().getNAS(sessionData.nasId);

                                            if (userData.userStatus == 0) {
                                                logger.info("Disconnecting user (user disabled): " + userData.userLogin + " sessionId: " + sessionData.sessionId);
                                                disconnectUser(NccUtils.long2ip(nasData.nasIP), NccUtils.long2ip(sessionData.framedIP), sessionData.sessionId);
                                            }

                                            if (Math.floor(userData.userDeposit) <= -Math.floor(userData.userCredit)) {
                                                logger.info("Disconnecting user (low deposit): " + userData.userLogin + " sessionId: " + sessionData.sessionId);
                                                disconnectUser(NccUtils.long2ip(nasData.nasIP), NccUtils.long2ip(sessionData.framedIP), sessionData.sessionId);
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
                radiusTimer.schedule(new NccRadiusTimer(), 1000, Ncc.radiusTimer * 1000);
            }
        });

        radiusWatchThread.start();

        start(true, true);
    }
}
