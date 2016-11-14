package com.NccSNMP;

import com.NccSystem.NccUtils;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.transport.TransportMappings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by root on 05.10.16.
 */
public class NccSNMP {

    private String ip;
    private String community;
    private TransportMapping transport;
    private CommunityTarget communityTarget;
    private Snmp snmp;

    public static final String ifIndex = "1.3.6.1.2.1.2.2.1.1";
    public static final String ifDescr = "1.3.6.1.2.1.2.2.1.2";
    public static final String ifType = "1.3.6.1.2.1.2.2.1.3";
    public static final String ifSpeed = "1.3.6.1.2.1.2.2.1.5";
    public static final String ifPhysAddress = "1.3.6.1.2.1.2.2.1.6";
    public static final String ifAdminStatus = "1.3.6.1.2.1.2.2.1.7";
    public static final String ifOperStatus = "1.3.6.1.2.1.2.2.1.8";
    public static final String ifInOctets = "1.3.6.1.2.1.2.2.1.10";
    public static final String ifHCInOctets = "1.3.6.1.2.1.31.1.1.1.6";
    public static final String ifInUcastPkts = "1.3.6.1.2.1.2.2.1.11";
    public static final String ifInNUcastPkts = "1.3.6.1.2.1.2.2.1.12";
    public static final String ifInDiscards = "1.3.6.1.2.1.2.2.1.13";
    public static final String ifInErrors = "1.3.6.1.2.1.2.2.1.14";
    public static final String ifOutOctets = "1.3.6.1.2.1.2.2.1.16";
    public static final String ifHCOutOctets = "1.3.6.1.2.1.31.1.1.1.10";
    public static final String ifOutUcastPkts = "1.3.6.1.2.1.2.2.1.17";
    public static final String ifOutNUcastPkts = "1.3.6.1.2.1.2.2.1.18";
    public static final String ifOutDiscards = "1.3.6.1.2.1.2.2.1.19";
    public static final String ifOutErrors = "1.3.6.1.2.1.2.2.1.20";

    public NccSNMP(String ip, String community) {
        this.ip = ip;
        this.community = community;

        try {
            transport = new DefaultUdpTransportMapping();
            transport.listen();

            communityTarget = new CommunityTarget();
            communityTarget.setCommunity(new OctetString(community));
            communityTarget.setVersion(SnmpConstants.version2c);
            communityTarget.setAddress(GenericAddress.parse("udp:" + ip + "/161"));
            communityTarget.setRetries(2);
            communityTarget.setTimeout(500);

            snmp = new Snmp(transport);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PDU preparePDU(OID oid) {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));

        return pdu;
    }

    private PDU prepareBulk(OID oid) {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GETBULK);
        pdu.setMaxRepetitions(500);
        pdu.setRequestID(new Integer32(1));

        return pdu;
    }

    private PDU send(String oid) {
        try {
            if (!transport.isListening()) transport.listen();
            ResponseEvent response = snmp.send(preparePDU(new OID(oid)), communityTarget, null);
            transport.close();

            if (response != null) {
                PDU responsePDU = response.getResponse();

                if (responsePDU != null) {
                    int errorStatus = responsePDU.getErrorStatus();
                    int errorIndex = responsePDU.getErrorIndex();
                    String errorStatusText = responsePDU.getErrorStatusText();

                    if (errorStatus == PDU.noError) {
                        return responsePDU;
                    } else {
                        return null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private HashMap<String, String> getBulk(String oid) {
        HashMap<String, String> items = new HashMap<>();

        try {
            if (!transport.isListening()) transport.listen();
            ResponseEvent response = snmp.send(prepareBulk(new OID(oid)), communityTarget);
            transport.close();

            if (response != null) {
                PDU responsePDU = response.getResponse();

                if (responsePDU != null) {

                    Vector vec = responsePDU.getVariableBindings();

                    for (Integer i = 0; i < vec.size(); i++) {
                        VariableBinding vb = (VariableBinding) vec.elementAt(i);
                        String var = vb.getOid().toString();
                        String val = vb.getVariable().toString();
                        if (var.contains(oid)) {
                            items.put(var, val);
                        }
                    }
                    return items;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HashMap<String, String> snmpWalk(String oid) {
        HashMap<String, String> result = new HashMap<>();

        return result;
    }

    public String getString(String oid) {
        PDU data = send(oid);
        if (data != null) {
            return data.get(0).getVariable().toString();
        }

        return "";
    }

    public HashMap<String, String> getStrings(String oid) {
        return getBulk(oid);
    }

    public void setInt(String oid, Integer val) {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid), new Integer32(val)));
        pdu.setType(PDU.SET);
        pdu.setRequestID(new Integer32(1));

        try {
            if (!transport.isListening()) transport.listen();
            ResponseEvent response = snmp.send(pdu, communityTarget);
            System.out.println(response);
            transport.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setString(String oid, String val) {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid), new OctetString(val)));
        pdu.setType(PDU.SET);
        pdu.setRequestID(new Integer32(1));

        try {
            if (!transport.isListening()) transport.listen();
            ResponseEvent response = snmp.send(pdu, communityTarget, null);
            transport.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
