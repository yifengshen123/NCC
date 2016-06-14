package com.NccAPI.NAS;

import com.NccAPI.NccAPI;
import com.NccNAS.NccNAS;
import com.NccNAS.NccNasData;
import com.NccNAS.NccNasException;
import com.NccNAS.NccNasType;
import com.mysql.management.util.Str;

import java.util.ArrayList;

public class NasServiceImpl implements NasService {

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
