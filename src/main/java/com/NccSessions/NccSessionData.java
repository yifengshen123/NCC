package com.NccSessions;

import com.NccSystem.NccAbstractData;
import java.sql.SQLException;

public class NccSessionData extends NccAbstractData<NccSessionData> {
    public Integer id;
    public String sessionId;
    public Long startTime;
    public Long stopTime;
    public Long acctInputOctets;
    public Long acctOutputOctets;
    public Integer terminateCause;
    public Long lastAlive;
    public Long sessionDuration;
    public Integer nasId;
    public Long framedIP;
    public String framedMAC;
    public Integer userId;
    public Long framedAgentId;
    public String framedCircuitId;
    public String framedRemoteId;
    public Integer userTariff;

    @Override
    public NccSessionData fillData(){
        NccSessionData sessionData = new NccSessionData();

        try {
            sessionData.id = rs.getInt("id");
            sessionData.sessionId = rs.getString("sessionId");
            sessionData.startTime = rs.getLong("startTime");
            sessionData.acctInputOctets = rs.getLong("acctInputOctets");
            sessionData.acctOutputOctets = rs.getLong("acctOutputOctets");
            sessionData.nasId = rs.getInt("nasId");
            sessionData.framedIP = rs.getLong("framedIP");
            sessionData.framedMAC = rs.getString("framedMAC");
            sessionData.framedAgentId = rs.getLong("framedAgentId");
            sessionData.framedCircuitId = rs.getString("framedCircuitId");
            sessionData.framedRemoteId = rs.getString("framedRemoteId");
            sessionData.userId = rs.getInt("userId");
            sessionData.lastAlive = rs.getLong("lastAlive");
            sessionData.sessionDuration = rs.getLong("sessionDuration");
            sessionData.userTariff = rs.getInt("userTariff");

            return sessionData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessionData;
    }
}
