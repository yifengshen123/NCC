package com.NccRadius;

import com.Ncc;
import com.NccAccounts.AccountData;
import com.NccAccounts.NccAccounts;
import com.NccDhcp.NccDhcpException;
import com.NccDhcp.NccDhcpLeaseData;
import com.NccDhcp.NccDhcpLeases;
import com.NccNAS.NccNAS;
import com.NccNAS.NccNasData;
import com.NccNAS.NccNasException;
import com.NccPools.NccPoolData;
import com.NccSessions.NccSessions;
import com.NccSessions.NccSessionsException;
import com.NccSystem.NccUtils;
import com.NccTariffScale.NccTariffScale;
import com.NccTariffScale.RateData;
import com.NccUsers.NccUserData;
import com.NccUsers.NccUsers;
import com.NccUsers.NccUsersException;
import org.apache.log4j.Logger;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;

class AccessThread implements Runnable {
    private static Logger logger = Logger.getLogger(NccRadius.class);
    private volatile RadiusPacket radiusPacket = new RadiusPacket();
    private AccessRequest req;
    private InetSocketAddress addr;

    public RadiusPacket getValue() {
        return radiusPacket;
    }

    private long startTime = System.nanoTime();

    private String reqUserName;
    private String reqUserPassword;
    private Integer reqPacketIdentifier;
    private String reqServiceType;

    private Integer packetType = RadiusPacket.ACCESS_REJECT;
    private NccNasData nasData;

    public AccessThread(AccessRequest accessRequest, InetSocketAddress socketAddress) {
        this.req = accessRequest;
        this.addr = socketAddress;
        this.reqUserName = accessRequest.getUserName();
        this.reqUserPassword = accessRequest.getUserPassword();
        this.reqPacketIdentifier = accessRequest.getPacketIdentifier();
        this.reqServiceType = accessRequest.getServiceType();
    }

    private void processOutbound(){
        NccDhcpLeases leases = new NccDhcpLeases();
        try {

            NccDhcpLeaseData leaseData = leases.getLeaseByIP(NccUtils.ip2long(reqUserName));
            if (leaseData != null) {

                if (leaseData.leaseUID == 0) {
                    logger.info("Login FAIL: userId=0");
                    return;
                }

                logger.debug("Found lease data");

                try {
                    NccUserData userData = new NccUsers().getUser(leaseData.leaseUID);

                    if (userData != null) {

                        logger.debug("Found user data");

                        if (userData.userStatus == 0) {
                            logger.info("Login FAIL: [" + userData.userLogin + "] user disabled");
                            return;
                        }

                        if (Math.floor(userData.userDeposit) <= -Math.floor(userData.userCredit)) {
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

                        radiusPacket.addAttribute("Acct-Interim-Interval", nasData.nasInterimInterval.toString());
                        radiusPacket.addAttribute("Idle-Timeout", nasData.nasIdleTimeout.toString());
                        radiusPacket.addAttribute("avpair", "subscriber:accounting-list=ipoe-isg-aaa");
                        radiusPacket.addAttribute("avpair", "ip:traffic-class=in access-group " + nasData.nasAccessGroupIn.toString() + " priority 201");
                        radiusPacket.addAttribute("avpair", "ip:traffic-class=out access-group " + nasData.nasAccessGroupOut.toString() + " priority 201");
                        packetType = RadiusPacket.ACCESS_ACCEPT;
                        logger.info("Login OK: " + reqUserName + " [" + userData.userLogin + "]");
                        return;
                    }

                    logger.info("Login FAIL: [" + reqUserName + "] user not found");
                    return;
                } catch (NccUsersException e) {
                    logger.debug("getUser error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                logger.info("Login FAIL: [" + reqUserName + "] lease not found");
                return;
            }

        } catch (NccDhcpException e) {
            e.printStackTrace();
        }

        logger.info("Login FAIL: " + reqUserName);
    }

    private void processFramed(){
        try {
            NccUserData userData = new NccUsers().getUser(reqUserName);

            AccountData accountData = new NccAccounts().getAccount(userData.accountId);
            try {
                if (req.verifyPassword(userData.userPassword)) {

                    if (userData.userStatus > 0) {

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
                    logger.info("Login FAIL: incorrect userPassword for '" + reqUserName + "' expected '" + userData.userPassword + "'");
                }
            } catch (RadiusException re) {
                re.printStackTrace();
            }

        } catch (NccUsersException e) {
            logger.info("User not found: '" + reqUserName + "'");
        }
    }

    @Override
    public void run() {

        if (Ncc.radiusLogLevel >= 6)
            logger.info("Access-Request '" + reqUserName + "' Service-Type '" + reqServiceType + "'");

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
            processOutbound();
        } else if (reqServiceType.equals("Framed") || reqServiceType.equals("2")) {
            logger.debug("Framed-User");
            processFramed();
        }

        radiusPacket.setPacketIdentifier(reqPacketIdentifier);
        radiusPacket.setPacketType(packetType);

        if (Ncc.radiusLogLevel >= 6)
            logger.info("Response time: " + new DecimalFormat("#.#########").format((double) (System.nanoTime() - startTime) / 1000000000) + " sec.");

    }
}
