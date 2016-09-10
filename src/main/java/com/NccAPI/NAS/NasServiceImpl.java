package com.NccAPI.NAS;

import com.NccAPI.NccAPI;
import com.NccAccounts.AccountData;
import com.NccNAS.NccNAS;
import com.NccNAS.NccNasData;
import com.NccNAS.NccNasException;
import com.NccNAS.NccNasType;

import java.util.ArrayList;

public class NasServiceImpl implements NasService {

    public Integer deleteNAS(String apiKey, Integer id) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        Integer did;

        try {
            did = new NccNAS().deleteNas(id);
        } catch (NccNasException e) {
            e.printStackTrace();
            return null;
        }

        return did;
    }

    public ApiNasData createNAS(String login, String key, String nasName, Integer nasType, Long nasIP, String nasSecret, Integer nasInterimInterval, Integer nasIdleTimeout, Integer nasAccessGroupIn, Integer nasAccessGroupOut) {

        if (!new NccAPI().checkPermission(login, key, "CreateNAS")) return null;

        NccNasData nasData = new NccNasData();

        ApiNasData apiNasData = new ApiNasData();
        apiNasData.data = new ArrayList<>();
        apiNasData.status = 0;

        nasData.nasName = nasName;
        nasData.nasType = nasType;
        nasData.nasIP = nasIP;
        nasData.nasSecret = nasSecret;
        nasData.nasInterimInterval = nasInterimInterval;
        nasData.nasIdleTimeout = nasIdleTimeout;
        nasData.nasAccessGroupIn = nasAccessGroupIn;
        nasData.nasAccessGroupOut = nasAccessGroupOut;

        if (nasName.equals("")) {
            apiNasData.message = "Empty NAS name";
            return apiNasData;
        }

        if (nasIP <= 0) {
            apiNasData.message = "Incorrect NAS IP";
            return apiNasData;
        }

        if (nasInterimInterval <= 0) {
            apiNasData.message = "InterimInterval must be >0";
            return apiNasData;
        }

        if (nasIdleTimeout <= 0) {
            apiNasData.message = "IdleTimeout must be >0";
            return apiNasData;
        }

        try {
            nasData.id = new NccNAS().createNas(nasData);

            if (nasData.id != null) {
                apiNasData.status = 1;
                apiNasData.data.add(nasData);
            } else {
                apiNasData.status = 0;
                apiNasData.message = "Error creating NAS";
            }
        } catch (NccNasException e) {
            e.printStackTrace();
        }

        return apiNasData;
    }

    public Integer updateNAS(String apiKey, Integer id, String nasName, Integer nasType, Long nasIP, String nasSecret, Integer nasInterimInterval) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        NccNasData nasData = new NccNasData();

        nasData.id = id;
        nasData.nasName = nasName;
        nasData.nasType = nasType;
        nasData.nasIP = nasIP;
        nasData.nasSecret = nasSecret;
        nasData.nasInterimInterval = nasInterimInterval;

        try {
            id = new NccNAS().updateNas(nasData);
        } catch (NccNasException e) {
            e.printStackTrace();
            return null;
        }

        return id;
    }

    public NccNasData getNAS(String apiKey, Integer id) {
        if (!new NccAPI().checkKey(apiKey)) return null;
        NccNasData nasData = null;
        try {
            nasData = new NccNAS().getNAS(id);
        } catch (NccNasException e) {

            return null;
        }

        return nasData;
    }

    public ArrayList<NccNasData> getNAS(String apiKey) {
        if (!new NccAPI().checkKey(apiKey)) return null;
        ArrayList<NccNasData> nas = null;

        try {
            nas = new NccNAS().getNAS();
        } catch (NccNasException e) {
            e.printStackTrace();
            return null;
        }

        return nas;
    }

    public ApiNasData getNAS(String login, String key) {

        if (!new NccAPI().checkPermission(login, key, "GetNAS")) return null;

        ApiNasData result = new ApiNasData();

        try {
            ArrayList<NccNasData> nasData = new NccNAS().getNAS();

            result.data = nasData;

        } catch (NccNasException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ApiNasTypeData getNasTypes(String login, String key) {

        if (!new NccAPI().checkPermission(login, key, "GetNAS")) return null;

        ApiNasTypeData result = new ApiNasTypeData();

        try {
            ArrayList<NccNasType> nasTypes = new NccNAS().getNASTypes();

            result.data = nasTypes;
        } catch (NccNasException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<NccNasType> getNASTypes(String apiKey) {
        if (!new NccAPI().checkKey(apiKey)) return null;
        ArrayList<NccNasType> types = null;

        try {
            types = new NccNAS().getNASTypes();
        } catch (NccNasException e) {
            e.printStackTrace();
            return null;
        }

        return types;
    }
}
