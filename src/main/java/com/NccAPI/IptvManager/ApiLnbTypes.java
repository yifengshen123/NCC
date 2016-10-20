package com.NccAPI.IptvManager;

import com.NccIptvManager.LnbTypesData;

import java.util.ArrayList;

/**
 * Created by root on 20.10.16.
 */
public class ApiLnbTypes {
    public ArrayList<LnbTypesData> data;
    public Integer status;
    public String message;

    public ApiLnbTypes(){
        data = new ArrayList<>();
        status = 1;
        message = "error";
    }
}
