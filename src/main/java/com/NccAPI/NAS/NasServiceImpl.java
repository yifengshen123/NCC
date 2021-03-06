package com.NccAPI.NAS;

import com.NccAPI.NccAPI;
import com.NccAccounts.AccountData;
import com.NccNAS.NccNAS;
import com.NccNAS.NccNasData;
import com.NccNAS.NccNasException;
import com.NccNAS.NccNasType;

import java.util.ArrayList;

public class NasServiceImpl implements NasService {

    public ApiNasData deleteNAS(String login, String key, Integer id) {

        ApiNasData apiNasData = new ApiNasData();
        apiNasData.data = new ArrayList<>();
        apiNasData.status = 0;

        if (!new NccAPI().checkPermission(login, key, "DeleteNAS")){
            apiNasData.message = "Permission denied";
            return apiNasData;
        }

        try {
            boolean did = new NccNAS().deleteNas(id);

            if(did){
                apiNasData.status = 1;
            }
        } catch (NccNasException e) {
            e.printStackTrace();
            apiNasData.message = e.getMessage();
        }

        return apiNasData;
    }

    public ApiNasData createNAS(String login, String key, String nasName, Integer nasType, Long nasIP, String nasSecret, Integer nasInterimInterval, Integer nasIdleTimeout, Integer nasAccessGroupIn, Integer nasAccessGroupOut) {

        ApiNasData apiNasData = new ApiNasData();
        apiNasData.data = new ArrayList<>();
        apiNasData.status = 0;

        if (!new NccAPI().checkPermission(login, key, "CreateNAS")){
            apiNasData.message = "Permission denied";
            return apiNasData;
        }

        NccNasData nasData = new NccNasData();

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

        if (nasInterimInterval < 60) {
            apiNasData.message = "InterimInterval must be >=60";
            return apiNasData;
        }

        if (nasIdleTimeout < 60) {
            apiNasData.message = "IdleTimeout must be >=60";
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
            apiNasData.message = e.getMessage();
        }

        return apiNasData;
    }

    public ApiNasData updateNAS(String login, String key, Integer id, String nasName, Integer nasType, Long nasIP, String nasSecret, Integer nasInterimInterval, Integer nasIdleTimeout, Integer nasAccessGroupIn, Integer nasAccessGroupOut) {
        ApiNasData apiNasData = new ApiNasData();
        apiNasData.data = new ArrayList<>();
        apiNasData.status = 0;

        if (!new NccAPI().checkPermission(login, key, "UpdateNAS")){
            apiNasData.message = "Permission denied";
            return apiNasData;
        }

        NccNasData nasData = new NccNasData();

        nasData.id = id;
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

        if (nasInterimInterval < 60) {
            apiNasData.message = "InterimInterval must be >=60";
            return apiNasData;
        }

        if (nasIdleTimeout < 60) {
            apiNasData.message = "IdleTimeout must be >=60";
            return apiNasData;
        }

        try {
            id = new NccNAS().updateNas(nasData);

            if (nasData.id != null) {
                apiNasData.status = 1;
                apiNasData.data.add(nasData);
            } else {
                apiNasData.status = 0;
                apiNasData.message = "Error updating NAS";
            }
        } catch (NccNasException e) {
            e.printStackTrace();
            apiNasData.message = e.getMessage();
        }

        return apiNasData;
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
