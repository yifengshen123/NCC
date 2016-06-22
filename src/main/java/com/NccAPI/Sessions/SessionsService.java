package com.NccAPI.Sessions;

import com.NccSessions.NccSessionData;

import java.util.ArrayList;

public interface SessionsService {

    public ArrayList<NccSessionData> getSessions(String apiKey);
    public NccSessionData getSessionByUID(String apiKey, Integer uid);
    public NccSessionData getSession(String apiKey, String sessionId);
}
