package com.NccAPI.System;

import com.NccAPI.NccAPI;
import com.NccAccounts.AccountData;

public class SystemServiceImpl implements SystemService {
    public SystemData pingAPI() {
        SystemData systemData = new SystemData();
        systemData.status = 1;
        return systemData;
    }

    public boolean authRequest(String login, String key) {
        AccountData accountData = new NccAPI().checkKey(login, key);

        if (accountData != null) return true;

        return false;
    }

    public boolean checkPermission(String login, String key, String permission) {
        return new NccAPI().checkPermission(login, key, permission);
    }

}
