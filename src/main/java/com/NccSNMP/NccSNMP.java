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

/**
 * Created by root on 05.10.16.
 */
public class NccSNMP {

    private String ip;
    private String community;
    private TransportMapping transport;
    private CommunityTarget communityTarget;
    private Snmp snmp;

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
            communityTarget.setTimeout(1000);

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

    private PDU send(String oid){
        try {
            ResponseEvent response = snmp.send(preparePDU(new OID(oid)), communityTarget, null);

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

    public String getString(String oid) {
        return send(oid).get(0).getVariable().toString();
    }

    public Integer getInteger(String oid){
        return send(oid).get(0).getVariable().toInt();
    }
}
