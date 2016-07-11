package com.NccAPI.NAS;

import com.NccAPI.NccAPI;
import com.NccNAS.NccNAS;
import com.NccNAS.NccNasData;
import com.NccNAS.NccNasException;
import com.NccNAS.NccNasType;

import java.util.ArrayList;

public class NasServiceImpl implements NasService {

    public Integer deleteNAS(String apiKey, Integer id){
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

    public Integer createNAS(String apiKey, String nasName, Integer nasType, Long nasIP, String nasSecret, Integer nasInterimInterval){
        if (!new NccAPI().checkKey(apiKey)) return null;

        NccNasData nasData = new NccNasData();
        Integer id;

        nasData.nasName = nasName;
        nasData.nasType = nasType;
        nasData.nasIP = nasIP;
        nasData.nasSecret = nasSecret;
        nasData.nasInterimInterval = nasInterimInterval;

        try {
            id = new NccNAS().createNas(nasData);
        } catch (NccNasException e) {
            e.printStackTrace();
            return null;
        }

        return id;
    }

    public Integer updateNAS(String apiKey, Integer id, String nasName, Integer nasType, Long nasIP, String nasSecret, Integer nasInterimInterval){
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

    public NccNasData getNAS(String apiKey, Integer id){
        if (!new NccAPI().checkKey(apiKey)) return null;
        NccNasData nasData = null;
        try {
            nasData = new NccNAS().getNAS(id);
        } catch (NccNasException e) {

            return null;
        }

        return nasData;
    }

    public ArrayList<NccNasData> getNAS(String apiKey){
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

    public ArrayList<NccNasType> getNASTypes(String apiKey){
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
