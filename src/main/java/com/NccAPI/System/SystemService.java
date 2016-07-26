package com.NccAPI.System;

public interface SystemService {
    SystemData pingAPI();

    boolean authRequest(String login, String key);

    boolean checkPermission(String login, String key, String permission);
}
