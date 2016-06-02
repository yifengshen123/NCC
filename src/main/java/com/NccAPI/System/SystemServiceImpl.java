package com.NccAPI.System;

public class SystemServiceImpl implements SystemService {
    public SystemData pingAPI(){
        SystemData systemData = new SystemData();
        systemData.status = 1;
        return systemData;
    }
}
