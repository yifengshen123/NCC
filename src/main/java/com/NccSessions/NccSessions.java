package com.NccSessions;

import com.NccPools.NccPoolData;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class NccSessions {

    private NccQuery query;
    private static Logger logger = Logger.getLogger(NccSessions.class);

    public NccSessions() throws NccSessionsException {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            throw new NccSessionsException("SQL error: " + e.getMessage());
        }
    }

    public boolean isAllocated(ArrayList<NccSessionData> sessions, Long ip) {
        if (sessions != null) {
            for (NccSessionData session : sessions) {
                if (Objects.equals(session.framedIP, ip)) return true;
            }
        }
        return false;
    }

    public Long getIPFromPool(ArrayList<NccPoolData> pools) {
        ArrayList<NccSessionData> sessions = new ArrayList<>();

        try {
            sessions = getSessions();
        } catch (NccSessionsException e) {
            e.printStackTrace();
            return null;
        }

        if (pools != null) {
            for (NccPoolData pool : pools) {
                if (pool != null) {
                    for (Long ip = pool.poolStart; ip <= pool.poolEnd; ip++) {
                        if (!isAllocated(sessions, ip)) return ip;
                    }
                }
            }
        }

        return null;
    }

    public NccSessionData getSession(String sessionID) throws NccSessionsException {

        return new NccSessionData().getData("SELECT * FROM nccSessions WHERE sessionId='" + StringEscapeUtils.escapeSql(sessionID) + "'");
    }

    public NccSessionData getSession(Integer uid) throws NccSessionsException {

        return new NccSessionData().getData("SELECT * FROM nccSessions WHERE userId=" + uid + " ORDER BY id DESC");
    }

    public ArrayList<NccSessionData> getSessions() throws NccSessionsException {

        return new NccSessionData().getDataList("SELECT * FROM nccSessions");
    }

    public ArrayList<Integer> startSession(NccSessionData sessionData) {

        try {
            NccQuery query = new NccQuery();

            ArrayList<Integer> ids = query.updateQuery("INSERT INTO nccSessions (" +
                    "sessionId, " +
                    "startTime, " +
                    "acctInputOctets, " +
                    "acctOutputOctets, " +
                    "nasId, " +
                    "framedIP, " +
                    "framedMAC, " +
                    "userId, " +
                    "lastAlive, " +
                    "sessionDuration, " +
                    "framedAgentId, " +
                    "framedCircuitId, " +
                    "framedRemoteId, " +
                    "userTariff" +
                    ") VALUES (" +
                    "'" + sessionData.sessionId + "', " +
                    "UNIX_TIMESTAMP(NOW()), " +
                    sessionData.acctInputOctets + ", " +
                    sessionData.acctOutputOctets + ", " +
                    sessionData.nasId + ", " +
                    sessionData.framedIP + ", " +
                    "'" + sessionData.framedMAC + "', " +
                    sessionData.userId + ", " +
                    "UNIX_TIMESTAMP(NOW()), " +
                    sessionData.sessionDuration + ", " +
                    sessionData.framedAgentId + ", " +
                    "'" + sessionData.framedCircuitId + "', " +
                    "'" + sessionData.framedRemoteId + "', " +
                    sessionData.userTariff +
                    ")");

            return ids;

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> stopSession(NccSessionData sessionData) {

        try {
            NccQuery query = new NccQuery();

            ArrayList<Integer> idsUpdate = query.updateQuery("INSERT INTO nccSessionsLog (" +
                    "userId, " +
                    "startTime, " +
                    "stopTime, " +
                    "acctInputOctets, " +
                    "acctOutputOctets, " +
                    "terminateCause, " +
                    "nasId, " +
                    "framedIP, " +
                    "framedMAC, " +
                    "framedAgentId, " +
                    "framedCircuitId, " +
                    "framedRemoteId, " +
                    "lastAlive, " +
                    "sessionDuration, " +
                    "sessionId, " +
                    "userTariff) VALUES (" +
                    sessionData.userId + ", " +
                    sessionData.startTime + ", " +
                    "UNIX_TIMESTAMP(), " +
                    sessionData.acctInputOctets + ", " +
                    sessionData.acctOutputOctets + ", " +
                    sessionData.terminateCause + ", " +
                    sessionData.nasId + ", " +
                    sessionData.framedIP + ", " +
                    "'" + sessionData.framedMAC + "', " +
                    sessionData.framedAgentId + ", " +
                    "'" + sessionData.framedCircuitId + "', " +
                    "'" + sessionData.framedRemoteId + "', " +
                    sessionData.lastAlive + ", " +
                    sessionData.sessionDuration + ", " +
                    "'" + sessionData.sessionId + "', " +
                    sessionData.userTariff +
                    ")");

            ArrayList<Integer> idsDelete = query.updateQuery("DELETE FROM nccSessions WHERE id=" + sessionData.id);

            return idsUpdate;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> updateSession(NccSessionData sessionData) {

        try {
            NccQuery query = new NccQuery();

            ArrayList<Integer> idsUpdate = query.updateQuery("UPDATE nccSessions SET " +
                    "acctInputOctets=" + sessionData.acctInputOctets + ", " +
                    "acctOutputOctets=" + sessionData.acctOutputOctets + ", " +
                    "framedIP=" + sessionData.framedIP + ", " +
                    "framedMAC='" + sessionData.framedMAC + "', " +
                    "userId=" + sessionData.userId + ", " +
                    "lastAlive=UNIX_TIMESTAMP(NOW()), " +
                    "sessionDuration=" + sessionData.sessionDuration + " " +
                    "WHERE id=" + sessionData.id);

            return idsUpdate;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccSessionData getSessionFromLog(String sessionID) {

        return new NccSessionData().getData("SELECT * FROM nccSessionsLog WHERE sessionId='" + StringEscapeUtils.escapeSql(sessionID) + "'");
    }

    public ArrayList<Integer> resumeSession(NccSessionData sessionData) {

        try {
            NccQuery query = new NccQuery();
            ArrayList<Integer> idsStart;

            NccSessionData resumeSession = getSessionFromLog(sessionData.sessionId);

            if (resumeSession == null) {
                idsStart = startSession(sessionData);
            } else {
                ArrayList<Integer> idsDelete = query.updateQuery("DELETE FROM nccSessionsLog WHERE id=" + sessionData.id);
                idsStart = startSession(sessionData);
            }

            return idsStart;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void cleanupSessions() {
        try {
            ArrayList<NccSessionData> sessions = getSessions();
            Long cleanupTime = System.currentTimeMillis() / 1000L - 120;

            for (NccSessionData session : sessions) {
                if (session.lastAlive < cleanupTime) {
                    System.out.println("Dead session: " + session.sessionId + " lastAlive: " + session.lastAlive);
                }
            }

        } catch (NccSessionsException e) {
            e.printStackTrace();
        }
    }
}
