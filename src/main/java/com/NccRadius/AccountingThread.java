package com.NccRadius;

import com.Ncc;
import com.NccDhcp.NccDhcpException;
import com.NccDhcp.NccDhcpLeaseData;
import com.NccDhcp.NccDhcpLeases;
import com.NccNAS.NccNAS;
import com.NccNAS.NccNasData;
import com.NccNAS.NccNasException;
import com.NccSessions.NccSessionData;
import com.NccSessions.NccSessions;
import com.NccSessions.NccSessionsException;
import com.NccSystem.NccUtils;
import com.NccUsers.NccUserData;
import com.NccUsers.NccUsers;
import com.NccUsers.NccUsersException;
import org.apache.log4j.Logger;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;

class AccountingThread implements Runnable {

    private static Logger logger = Logger.getLogger(NccRadius.class);

    private volatile RadiusPacket radiusPacket = new RadiusPacket();
    private AccountingRequest accountingRequest;
    private InetSocketAddress socketAddress;

    private String userLogin = "";
    private Integer userId = 0;
    private String sessionID = "";
    private String nasPort = "";
    private String nasIP = "";
    private String nasPortType = "";
    private String nasIdentifier = "";
    private String framedIP = "";
    private String framedMAC = "";
    private Long acctInputOctets;
    private Long acctOutputOctets;
    private Integer acctInputGigawords;
    private Integer acctOutputGigawords;
    private String acctSessionTime = "";
    private String callingStation = "";
    private String calledStation = "";
    private String framedProtocol = "";
    private String serviceType = "";
    private String eventTimestamp = "";
    private String acctAuthentic = "";

    private NccNasData nasData = null;
    private NccSessionData sessionData = new NccSessionData();
    private NccDhcpLeaseData leaseData = null;
    private ArrayList<NccDhcpLeaseData> leases = null;

    public RadiusPacket getValue() {
        return radiusPacket;
    }

    private void startSession(){

        if (userLogin.equals("")) {
            if (Ncc.radiusLogLevel >= 5)
                logger.error("Empty User-Name session: '" + sessionID + "'");
            return;
        }

        try {
            NccSessionData checkSession = new NccSessions().getSession(sessionID);
            if (checkSession != null) {
                logger.error("Duplicate session: '" + sessionID + "'");
                return;
            }
        } catch (NccSessionsException e) {
            e.printStackTrace();
        }

        sessionData.nasId = nasData.id;

        sessionData.framedIP = (framedIP != null) ? NccUtils.ip2long(framedIP) : 0L;
        sessionData.framedMAC = (framedMAC != null) ? framedMAC : "00:00:00:00:00:00";
        sessionData.acctInputOctets = 0L;
        sessionData.acctOutputOctets = 0L;
        sessionData.sessionId = sessionID;
        sessionData.startTime = System.currentTimeMillis() / 1000L;
        sessionData.lastAlive = sessionData.startTime;
        sessionData.sessionDuration = 0L;

        if (serviceType.equals("Outbound-User") || serviceType.equals("5")) {

            try {
                leaseData = new NccDhcpLeases().getLeaseByIP(sessionData.framedIP);

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
            }
        } else if (serviceType.equals("Framed") || serviceType.equals("2")) {
            try {
                NccUserData userData = new NccUsers().getUser(userLogin);

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
            logger.error("Unknown Service-Type: " + serviceType);
        }
    }

    private void stopSession(){
        if (userLogin.equals("")) {
            if (Ncc.radiusLogLevel >= 5)
                logger.error("Empty User-Name session: '" + sessionID + "'");
            return;
        }

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
    }

    private void updateSession(){
        if (userLogin.equals("")) {
            if (Ncc.radiusLogLevel >= 5)
                logger.error("Empty User-Name session: '" + sessionID + "'");
            return;
        }

        acctInputOctets = Long.parseLong(accountingRequest.getAttributeValue("Acct-Input-Octets"));
        acctOutputOctets = Long.parseLong(accountingRequest.getAttributeValue("Acct-Output-Octets"));

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

        acctSessionTime = accountingRequest.getAttributeValue("Acct-Session-Time");

        try {
            sessionData = new NccSessions().getSession(sessionID);
        } catch (NccSessionsException e) {
            e.printStackTrace();
        }

        if(sessionData==null){
            logger.error("Session not found: '" + sessionID + "'");
            try {
                NccSessionData resumeSession = new NccSessions().getSessionFromLog(sessionID);

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
                    logger.info("No session to resume");
                    NccRadius.disconnectUser(nasIP, userLogin, sessionID);
                }
            } catch (NccSessionsException e) {
                e.printStackTrace();
            }
        }

        if (serviceType.equals("Outbound-User") || serviceType.equals("5")) {
            try {
                leaseData = new NccDhcpLeases().getLeaseByIP(NccUtils.ip2long(framedIP));

                if (leaseData != null) {

                    if (Ncc.radiusLogLevel >= 6)
                        logger.info("Lease found: " + NccUtils.long2ip(leaseData.leaseIP));

                    if (sessionData != null) {

                        sessionData.userId = leaseData.leaseUID;
                        sessionData.framedMAC = leaseData.leaseClientMAC;
                        sessionData.framedRemoteId = leaseData.leaseRemoteID;
                        sessionData.framedCircuitId = leaseData.leaseCircuitID;
                        sessionData.acctInputOctets = acctInputOctets;
                        sessionData.acctOutputOctets = acctOutputOctets;
                        sessionData.lastAlive = System.currentTimeMillis() / 1000L;
                        sessionData.sessionDuration = sessionData.lastAlive - sessionData.startTime;

                        try {
                            new NccSessions().updateSession(sessionData);
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

                            if (Ncc.radiusLogLevel >= 6)
                                logger.info("Session found: '" + sessionID + "'");

                            //new NccSessions().stopSession(sessionData);
                        } else {
                            logger.error("Session not found: '" + sessionID + "'");
                        }

                        NccRadius.disconnectUser(nasIP, userLogin, sessionID);

                    } catch (NccSessionsException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NccDhcpException e) {
                e.printStackTrace();
            }
        } else if (serviceType.equals("Framed") || serviceType.equals("2")) {

            if (sessionData != null) {

                sessionData.framedIP = (framedIP != null) ? NccUtils.ip2long(framedIP) : 0L;
                sessionData.framedMAC = (framedMAC != null) ? framedMAC : "00:00:00:00:00:00";
                sessionData.acctInputOctets = acctInputOctets;
                sessionData.acctOutputOctets = acctOutputOctets;
                sessionData.lastAlive = System.currentTimeMillis() / 1000L;
                sessionData.sessionDuration = sessionData.lastAlive - sessionData.startTime;

                try {
                    new NccSessions().updateSession(sessionData);
                } catch (NccSessionsException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public AccountingThread(AccountingRequest accReq, InetSocketAddress accClient){
        this.accountingRequest = accReq;
        this.socketAddress = accClient;
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

                try {
                    nasData = new NccNAS().getNasByIP(NccUtils.ip2long(nasIP));

                    if (nasData == null) {
                        logger.error("NAS not found: " + nasIP);
                        return;
                    }
                } catch (NccNasException e) {
                    e.printStackTrace();
                }

                if (statusType.intValue() == AccountingRequest.ACCT_STATUS_TYPE_START) {
                    logger.info("Session start: '" + userLogin + "' sessionId=" + sessionID + " nasIP=" + nasIP + " nasPort=" + nasPort + " framedIP=" + framedIP + " framedMAC=" + framedMAC);
                    startSession();
                } else if (statusType.intValue() == AccountingRequest.ACCT_STATUS_TYPE_STOP) {
                    logger.info("Session stop: '" + userLogin + "' sessionId='" + sessionID + "' nasIP=" + nasIP + " nasPort=" + nasPort + " framedIP=" + framedIP);
                    stopSession();
                } else if (statusType.intValue() == AccountingRequest.ACCT_STATUS_TYPE_INTERIM_UPDATE) {
                    if (Ncc.radiusLogLevel >= 5)
                        logger.info("Session update: '" + userLogin + "' sessionId=" + sessionID + " nasIP=" + nasIP + " nasPort=" + nasPort + " framedIP=" + framedIP + " framedMAC=" + framedMAC);
                    updateSession();
                }

                break;
            default:
                break;
        }

    }
}
