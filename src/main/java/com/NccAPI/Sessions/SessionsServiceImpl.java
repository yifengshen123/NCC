package com.NccAPI.Sessions;

import com.NccAPI.NccAPI;
import com.NccSessions.NccSessionData;
import com.NccSessions.NccSessions;
import com.NccSessions.NccSessionsException;

import java.util.ArrayList;

public class SessionsServiceImpl implements SessionsService {

    public ArrayList<NccSessionData> getSessions(String apiKey){
        if (!new NccAPI().checkKey(apiKey)) return null;
        try {
            return new NccSessions().getSessions();
        } catch (NccSessionsException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NccSessionData getSessionByUID(String apiKey, Integer uid){
        if (!new NccAPI().checkKey(apiKey)) return null;
        try {
            return new NccSessions().getSession(uid);
        } catch (NccSessionsException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NccSessionData getSession(String apiKey, String sessionId){
        if (!new NccAPI().checkKey(apiKey)) return null;
        try {
            return new NccSessions().getSession(sessionId);
        } catch (NccSessionsException e) {
            e.printStackTrace();
        }
        return null;
    }
}
